package com.sysbot32.netptalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityChatBinding

private val chatMap: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf()

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatRoom: String
    private lateinit var chatMessages: MutableList<ChatMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        if (title != null) {
            chatRoom = title
            chatMessages = getChatMessages(chatRoom)

            val chatMessageAdapter: ChatMessageAdapter = ChatMessageAdapter(this, chatMessages)
            binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewChat.adapter = chatMessageAdapter

            binding.buttonSubmit.setOnClickListener {
                val content: String = binding.editTextChat.text.toString()
                chatClient?.sendMessage(content, chatRoom)
            }
        } else {
            finish()
        }
    }
}

fun getChatMessages(chatRoom: String): MutableList<ChatMessage> {
    var chatMessages = chatMap[chatRoom]
    if (chatMessages == null) {
        chatMessages = mutableListOf()
        chatMap[chatRoom] = chatMessages
    }
    return chatMessages
}
