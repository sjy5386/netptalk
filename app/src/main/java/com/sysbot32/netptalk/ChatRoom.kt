package com.sysbot32.netptalk

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemChatRoomBinding
import org.json.JSONArray
import org.json.JSONObject

val chatRooms: MutableList<ChatRoom> = mutableListOf()

data class ChatRoom(val title: String) {
    fun toJSONObject(): JSONObject {
        val jsonArray = JSONArray()
        getChatMessages(title).forEach {
            jsonArray.put(it.toJSONObject())
        }
        return JSONObject()
            .put("title", title)
            .put("chatMessages", jsonArray)
    }
}

class ChatRoomViewHolder(val binding: ItemChatRoomBinding) :
    RecyclerView.ViewHolder(binding.root) {
}

class ChatRoomAdapter(private val context: Context, private val chatRooms: MutableList<ChatRoom>) :
    RecyclerView.Adapter<ChatRoomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemChatRoomBinding =
            ItemChatRoomBinding.inflate(layoutInflater, parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom: ChatRoom = chatRooms[position]
        holder.binding.textTitle.text = chatRoom.title
        holder.binding.chatRoom.setOnClickListener {
            val chatIntent: Intent = Intent(context, ChatActivity::class.java)
            chatIntent.putExtra("title", chatRoom.title)
            context.startActivity(chatIntent)
        }
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }
}
