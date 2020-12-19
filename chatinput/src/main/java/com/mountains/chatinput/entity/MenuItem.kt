package com.mountains.chatinput.entity

import androidx.annotation.DrawableRes

open class MenuItem (
    val tag:Any,
    @DrawableRes
    val icon:Int,
    val label:String
)