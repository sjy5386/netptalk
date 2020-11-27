package com.sysbot32.netptalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sysbot32.netptalk.databinding.ItemEmoticonBinding

val emoticons: List<Int> = listOf(
    R.drawable.normal,
    R.drawable.shouting,
    R.drawable.computer,
    R.drawable.heart,
    R.drawable.open_a_bag,
    R.drawable.dislike,
    R.drawable.thumbs_up,
    R.drawable.hugging,
    R.drawable.graduation,
    R.drawable.eating,
)

class EmoticonViewHolder(val binding: ItemEmoticonBinding) : RecyclerView.ViewHolder(binding.root)

class EmoticonAdapter(private val context: Context, private val emoticons: List<Int>) :
    RecyclerView.Adapter<EmoticonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmoticonViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemEmoticonBinding =
            ItemEmoticonBinding.inflate(layoutInflater, parent, false)
        return EmoticonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmoticonViewHolder, position: Int) {
        val emoticon = emoticons[position]
        holder.binding.imageEmoticon.setImageResource(emoticon)
        holder.binding.imageEmoticon.setOnClickListener {
            val recyclerViewEmoticon = chatActivity.binding.recyclerViewEmoticon
            val chatRoom: String = chatActivity.chatRoom
            recyclerViewEmoticon.visibility = View.GONE
            chatClient?.sendMessage("emoticon", emoticon.toString(), chatRoom)
        }
    }

    override fun getItemCount(): Int {
        return emoticons.size
    }
}
