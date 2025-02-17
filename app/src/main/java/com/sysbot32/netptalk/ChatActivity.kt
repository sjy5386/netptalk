package com.sysbot32.netptalk

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityChatBinding

lateinit var chatActivity: ChatActivity

const val REQUEST_CODE_IMAGE: Int = 105
const val REQUEST_CODE_FILE: Int = 102

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chatRoom: String
    private lateinit var chatMessages: MutableList<ChatMessage>
    var status: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        if (title != null) {
            chatRoom = title
            chatMessages = getChatMessages(chatRoom)

            binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewChat.adapter = getChatMessageAdapter()

            binding.buttonSubmit.setOnClickListener {
                val content: String = binding.editTextChat.text.toString()
                if (content != "") {
                    binding.editTextChat.setText("")
                    chatClient?.sendMessage("text", content, chatRoom)
                }
            }
        } else {
            finish()
        }

        binding.recyclerViewEmoticon.setHasFixedSize(true)
        binding.recyclerViewEmoticon.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEmoticon.adapter = EmoticonAdapter(this, emoticons)
    }

    override fun onResume() {
        super.onResume()
        chatActivity = this
        status = true
    }

    override fun onPause() {
        super.onPause()
        status = false
        save(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        save(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuEmoticon -> {
                if (binding.recyclerViewEmoticon.visibility == View.VISIBLE) {
                    binding.recyclerViewEmoticon.visibility = View.GONE
                } else {
                    binding.recyclerViewEmoticon.visibility = View.VISIBLE
                }
            }
            R.id.menuImage -> {
                startActivityForResult(
                    Intent().setAction(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
                    ), REQUEST_CODE_IMAGE
                )
            }
            R.id.menuFile -> {
                startActivityForResult(
                    Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*"), REQUEST_CODE_FILE
                )
            }
            R.id.menuInvite -> {
                val editInvitee: EditText = EditText(this)
                AlertDialog.Builder(this)
                    .setTitle(R.string.menu_invite)
                    .setMessage(R.string.alert_message_invite)
                    .setView(editInvitee)
                    .setPositiveButton(R.string.button_ok) { dialogInterface: DialogInterface, i: Int ->
                        chatClient?.inviteToChatRoom(chatRoom, editInvitee.text.toString())
                    }
                    .setNegativeButton(R.string.button_cancel) { dialogInterface: DialogInterface, i: Int ->
                    }.create().show()
            }
            R.id.menuLeave -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.menu_leave)
                    .setMessage(R.string.alert_message_leave)
                    .setPositiveButton(R.string.button_yes) { dialogInterface: DialogInterface, i: Int ->
                        chatClient?.leaveChatRoom(chatRoom)
                        var index: Int = -1
                        for (i in 0..chatRooms.size) {
                            if (chatRooms[i].title == chatRoom) {
                                index = i
                                break
                            }
                        }
                        if (index >= 0) {
                            chatRooms.removeAt(index)
                            mainActivity.chatRoomAdapter.notifyItemRemoved(index)
                        }
                        finish()
                    }
                    .setNegativeButton(R.string.button_no) { dialogInterface: DialogInterface, i: Int ->
                    }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == RESULT_OK) && (data != null) && (data.data != null)) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> {
                    val uri: Uri = data.data!!
                    Thread() {
                        val bitmap = resizeBitmap(loadBitmapByUri(uri), 1280, 720)
                        val content: String = bitmapToBase64(bitmap)
                        chatClient?.sendMessage("image", content, chatRoom)
                    }.start()
                    Toast.makeText(this, R.string.toast_sending_image, Toast.LENGTH_SHORT).show()
                }
                REQUEST_CODE_FILE -> {
                    val uri = data.data!!
                    Thread() {
                        chatClient?.sendFile(chatRoom, ChatFile(this, uri))
                    }.start()
                }
            }
        }
    }

    private fun getChatMessageAdapter(): ChatMessageAdapter {
        var chatMessageAdapter = chatMessageAdapterMap[chatRoom]
        if (chatMessageAdapter == null) {
            chatMessageAdapter = ChatMessageAdapter(this, chatMessages)
            chatMessageAdapterMap[chatRoom] = chatMessageAdapter
        }
        return chatMessageAdapter
    }
}
