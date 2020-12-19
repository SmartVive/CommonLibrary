package com.mountains.chatinput.entity

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

class ChatInputConfig private constructor() {
    //菜单
    var menuItemList = listOf<MenuItem>()
    //拍照保存路径
    var captureStrategy: CaptureStrategy? = null
    //输入框文字颜色
    @ColorInt
    var contentTextColor:Int? = null
    //发送按钮背景
    @DrawableRes
    var sendButtonBackground:Int? = null
    //菜单高度
    var menuLayoutHeight:Int? = null
    //支持语音信息
    var isSupportVoiceMessage = true
    //菜单按钮一行多少列
    var menuColumns = 4



    companion object {
        class Builder {
            private var menuItemList = mutableListOf<MenuItem>()
            private var captureStrategy: CaptureStrategy? = null
            @ColorInt
            private var contentTextColor:Int? = null
            @DrawableRes
            private var sendButtonBackground:Int? = null
            private var menuLayoutHeight:Int? = null
            private var isSupportVoiceMessage = true
            private var menuColumns = 4

            fun setMenuItemList(menuItemList: List<MenuItem>): Builder {
                this.menuItemList = menuItemList as MutableList<MenuItem>
                return this
            }

            fun addMenuItem(menuItem: MenuItem): Builder {
                this.menuItemList.add(menuItem)
                return this
            }

            fun setCaptureStrategy(captureStrategy: CaptureStrategy): Builder {
                this.captureStrategy = captureStrategy
                return this
            }

            fun setMenuColumns(columns:Int): Builder{
                this.menuColumns = columns
                return this
            }

            fun setContentTextColor(@ColorInt color:Int): Builder {
                this.contentTextColor = color
                return this
            }

            fun setSendButtonBackground(@DrawableRes sendButtonBackground:Int): Builder {
                this.sendButtonBackground = sendButtonBackground
                return this
            }

            fun setMenuLayoutHeight(menuLayoutHeight:Int): Builder {
                this.menuLayoutHeight = menuLayoutHeight
                return this
            }

            fun isSupportVoiceMessage(isSupport:Boolean): Builder {
                this.isSupportVoiceMessage = isSupport
                return this
            }

            fun build(): ChatInputConfig {
                val chatInputConfig =
                    ChatInputConfig()
                chatInputConfig.menuItemList = menuItemList;
                chatInputConfig.captureStrategy = captureStrategy
                chatInputConfig.contentTextColor = contentTextColor
                chatInputConfig.sendButtonBackground = sendButtonBackground
                chatInputConfig.menuLayoutHeight = menuLayoutHeight
                chatInputConfig.isSupportVoiceMessage = isSupportVoiceMessage
                chatInputConfig.menuColumns = menuColumns
                return chatInputConfig
            }

        }
    }
}