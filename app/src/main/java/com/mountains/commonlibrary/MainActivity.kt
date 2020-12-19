package com.mountains.commonlibrary

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mountains.chatinput.entity.ChatInputConfig
import com.mountains.chatinput.ChatInputView
import com.mountains.chatinput.entity.MenuItem
import com.mountains.chatinput.menu.PresetMenuItem.CallMenuItem
import com.mountains.chatinput.menu.PresetMenuItem.ChosePhotoMenuItem
import com.mountains.chatinput.menu.PresetMenuItem.TakePhotoMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chatInputConfig = ChatInputConfig.Companion.Builder()
            .addMenuItem(ChosePhotoMenuItem())
            .addMenuItem(TakePhotoMenuItem())
            .addMenuItem(CallMenuItem())
            .setContentTextColor(Color.CYAN)
            .setSendButtonBackground(R.drawable.chatinput_shape_record)
            .build()
        chatInputView.initChatInputConfig(chatInputConfig)

        chatInputView.menuItemListener = object  : ChatInputView.OnMenuItemClickListener(){
            override fun sendImageMessage(files: List<String>) {
                super.sendImageMessage(files)
                Toast.makeText(this@MainActivity,files.toString(), Toast.LENGTH_LONG).show()
            }

            override fun voiceCall() {
                super.voiceCall()
                Toast.makeText(this@MainActivity,"语音通话", Toast.LENGTH_LONG).show()

            }

            override fun videoCall() {
                super.videoCall()
                Toast.makeText(this@MainActivity,"视频通话", Toast.LENGTH_LONG).show()
            }

            override fun otherMenuItem(menuItem: MenuItem) {
                super.otherMenuItem(menuItem)
                Toast.makeText(this@MainActivity,"点击了:${menuItem.label}",Toast.LENGTH_LONG).show()
            }
        }

        chatInputView.messageListener = object : ChatInputView.OnMessageListener{
            override fun sendTextMessage(text: String) {
                Toast.makeText(this@MainActivity,"发送文字消息：$text", Toast.LENGTH_LONG).show()
            }

            override fun sendVoiceMessage(file: File, duration: Int) {
                Toast.makeText(this@MainActivity,"发送语音消息：${file.absolutePath},时长：$duration", Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            super.onBackPressed()
        }
    }
}