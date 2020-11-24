package com.sysbot32.netptalk

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemChatRoomBinding

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
        holder.binding.textTitle.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }
}