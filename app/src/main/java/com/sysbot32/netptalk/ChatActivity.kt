package com.sysbot32.netptalk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityChatBinding

lateinit var chatActivity: ChatActivity

const val REQUEST_CODE_IMAGE: Int = 105

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemEmoticon) {
            binding.recyclerViewEmoticon.visibility = View.VISIBLE
        } else if (item.itemId == R.id.itemImage) {
            startActivityForResult(
                Intent().setAction(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
                ), REQUEST_CODE_IMAGE
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == RESULT_OK) && (data != null) && (data.data != null)) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                val uri: Uri = data.data!!
                Thread() {
                    val bitmap = loadBitmapByUri(uri)
                    val content: String = bitmapToBase64(bitmap)
                    chatClient?.sendMessage("image", content, chatRoom)
                }.start()
                Toast.makeText(this, R.string.toast_sending_image, Toast.LENGTH_SHORT).show()
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
