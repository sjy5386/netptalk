package com.sysbot32.netptalk

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.sysbot32.netptalk.databinding.SpeechBubbleBinding

class SpeechBubble(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val binding: SpeechBubbleBinding

    init {
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = SpeechBubbleBinding.inflate(layoutInflater)
    }

    fun setProfileImage(image: Bitmap) {
        binding.profileImage.setImageBitmap(image)
    }

    fun setUsername(username: String) {
        binding.username.text = username
    }

    fun setContent(content: View) {
        binding.content.removeAllViews()
        binding.content.addView(content)
    }
}
