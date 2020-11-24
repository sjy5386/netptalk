package com.sysbot32.netptalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityChatBinding

var chatActivity: ChatActivity? = null

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
                    chatClient?.sendMessage(content, chatRoom)
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
}
