package com.sysbot32.netptalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var title: String = "room1"
    private val chatMessages: MutableList<ChatMessage> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatMessageAdapter: ChatMessageAdapter = ChatMessageAdapter(this, chatMessages)
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChat.adapter = chatMessageAdapter

        binding.buttonSubmit.setOnClickListener {
            val content: String = binding.editTextChat.text.toString()
            chatClient?.sendMessage(content, title)
        }
    }
}
