package com.sysbot32.netptalk

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemChatMessageBinding
import org.json.JSONObject
import java.io.ByteArrayInputStream

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
            holder.binding.imageMessage.visibility = View.GONE
        } else if (chatMessage.chatType == "image") {
            val buf: ByteArray = Base64.decode(chatMessage.content, 0)
            val byteArrayInputStream = ByteArrayInputStream(buf)
            val bitmap = BitmapFactory.decodeStream(byteArrayInputStream)
            holder.binding.imageMessage.setImageBitmap(bitmap)
            holder.binding.textMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }
}
