package com.sysbot32.netptalk

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemChatMessageBinding
import org.json.JSONObject

data class ChatMessage(
    val username: String,
    val chatType: String,
    val content: String,
    val chatRoom: String
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("username"),
        jsonObject.getString("chatType"),
        jsonObject.getString("content"),
        jsonObject.getString("chatRoom")
    )
}

class ChatMessageViewHolder(val binding: ItemChatMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
}

class ChatMessageAdapter(
    private val context: Context,
    private val chatMessages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatMessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemChatMessageBinding =
            ItemChatMessageBinding.inflate(layoutInflater, parent, false)
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage: ChatMessage = chatMessages[position]
        holder.binding.textUsername.text = chatMessage.username
        if (chatMessage.chatType == "text") {
            holder.binding.textMessage.text = chatMessage.content
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }
}
