package com.mountains.chatinput.menu

import com.mountains.chatinput.R
import com.mountains.chatinput.entity.MenuItem


object PresetMenuItem {

    //相册
    const val TAG_PHOTO = 0
    //拍摄
    const val TAG_CAMERA = 1
    //在线音视频
    const val TAG_CALL = 2

    class ChosePhotoMenuItem: MenuItem(TAG_PHOTO, R.drawable.chatinput_ic_chat_photo,"相册")


    class TakePhotoMenuItem: MenuItem(TAG_CAMERA, R.drawable.chatinput_ic_chat_camera,"拍摄")


    class CallMenuItem: MenuItem(TAG_CALL, R.drawable.chatinput_ic_chat_video,"视频通话")
}
