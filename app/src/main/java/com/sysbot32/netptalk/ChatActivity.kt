package com.sysbot32.netptalk

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityChatBinding
import java.io.ByteArrayOutputStream

var chatActivity: ChatActivity? = null

const val REQUEST_CODE_IMAGE: Int = 105

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chatRoom: String
    private lateinit var chatMessages: MutableList<ChatMessage>
    lateinit var chatMessageAdapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        if (title != null) {
            chatRoom = title
            chatMessages = getChatMessages(chatRoom)

            chatMessageAdapter = ChatMessageAdapter(this, chatMessages)
            binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewChat.adapter = chatMessageAdapter

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
    }

    override fun onResume() {
        super.onResume()
        chatActivity = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemImage) {
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
                val bitmap =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                    else
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val buf: ByteArray = byteArrayOutputStream.toByteArray()
                val content: String = Base64.encodeToString(buf, 0)
                chatClient?.sendMessage("image", content, chatRoom)
            }
        }
    }
}
