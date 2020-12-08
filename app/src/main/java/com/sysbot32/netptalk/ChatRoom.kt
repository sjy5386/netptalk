package com.sysbot32.netptalk

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemChatRoomBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

val chatRooms: MutableList<ChatRoom> = mutableListOf()

data class ChatRoom(val title: String) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("title")
    ) {
        val jsonArray = jsonObject.getJSONArray("chatMessages")
        for (it in 0 until jsonArray.length()) {
            getChatMessages(title).add(ChatMessage(jsonArray.getJSONObject(it)))
        }
    }

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

fun save(context: Context) {
    val jsonArray = JSONArray()
    chatRooms.forEach {
        jsonArray.put(it.toJSONObject())
    }
    writeFile(
        File(context.filesDir, "chatRooms.json"),
        jsonArray.toString().toByteArray(StandardCharsets.UTF_8)
    )
}

fun load(context: Context) {
    val file = File(context.filesDir, "chatRooms.json")
    if (file.exists()) {
        chatRooms.forEach {
            chatRooms.remove(it)
        }
        val jsonArray = JSONArray(String(readFile(file), StandardCharsets.UTF_8))
        for (it in 0 until jsonArray.length()) {
            chatRooms.add(ChatRoom(jsonArray.getJSONObject(it)))
        }
    }
}
