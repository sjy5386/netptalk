package com.sysbot32.netptalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemChatMessageBinding
import org.json.JSONObject
import java.text.SimpleDateFormat

private val chatMessagesMap: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf()
val chatMessageAdapterMap: MutableMap<String, ChatMessageAdapter> = mutableMapOf()

data class ChatMessage(
    val username: String,
    val chatType: String,
    val content: String,
    val chatRoom: String,
    val timestamp: Long
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("username"),
        jsonObject.getString("chatType"),
        jsonObject.getString("content"),
        jsonObject.getString("chatRoom"),
        jsonObject.getLong("timestamp")
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
        if ((chatMessage.chatType != "system") && (chatMessage.username != username)) {
            holder.binding.textUsername.text = chatMessage.username
            holder.binding.textTimestamp.text =
                SimpleDateFormat("a h:mm").format(chatMessage.timestamp)
            holder.binding.textUsername.visibility = View.VISIBLE
            holder.binding.imageProfile.visibility = View.VISIBLE
            holder.binding.textTimestamp.visibility = View.VISIBLE
        }
        when (chatMessage.chatType) {
            "text" -> {
                if (chatMessage.username != username) {
                    holder.binding.textMessage.text = chatMessage.content
                    holder.binding.textMessage.visibility = View.VISIBLE
                } else {
                    holder.binding.textMyMessage.text = chatMessage.content
                    holder.binding.textMyMessage.visibility = View.VISIBLE
                }
            }
            "emoticon" -> {
                val emoticon = chatMessage.content.toInt()
                if (emoticons.contains(emoticon)) {
                    holder.binding.imageMessage.setImageResource(emoticon)
                    holder.binding.imageMessage.visibility = View.VISIBLE
                }
            }
            "image" -> {
                val bitmap = base64ToBitmap(chatMessage.content)
                holder.binding.imageMessage.setImageBitmap(bitmap)
                holder.binding.imageMessage.visibility = View.VISIBLE
            }
            "system" -> {
                holder.binding.textSystem.text = chatMessage.content
                holder.binding.textSystem.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }
}

fun getChatMessages(chatRoom: String): MutableList<ChatMessage> {
    var chatMessages = chatMessagesMap[chatRoom]
    if (chatMessages == null) {
        chatMessages = mutableListOf()
        chatMessagesMap[chatRoom] = chatMessages
    }
    return chatMessages
}
