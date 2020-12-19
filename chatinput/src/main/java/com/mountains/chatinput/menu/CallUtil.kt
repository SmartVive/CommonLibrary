package com.mountains.chatinput.menu

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.PopupWindow
import com.mountains.chatinput.R
import com.mountains.chatinput.util.AlphaUtil

object CallUtil {
    const val TYPE_VOICE_CALL = 0
    const val TYPE_VIDEO_CALL = 1

    /**
     * 选择音视或视频频通话
     */
    fun showSelectCallTypePopup(parent: ViewGroup,context: Context,e:(type:Int)->Unit) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.chatinput_popup_call_select, parent, false)
        val popupWindow = PopupWindow(
            view, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupWindow.animationStyle =
            R.style.PopupWindowAnim
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (context is Activity) {
            AlphaUtil.setAlpha(
                context as Activity?,
                0.5f
            )
            popupWindow.setOnDismissListener {
                AlphaUtil.setAlpha(
                    context as Activity?,
                    1f
                )
            }
        }
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0)
        view.findViewById<View>(R.id.btnVoiceCall)
            .setOnClickListener {
                e.invoke(TYPE_VOICE_CALL)
                popupWindow.dismiss()
            }
        view.findViewById<View>(R.id.btnVideoCall)
            .setOnClickListener {
                e.invoke(TYPE_VIDEO_CALL)
                popupWindow.dismiss()
            }
        view.findViewById<View>(R.id.btnCancel)
            .setOnClickListener { popupWindow.dismiss() }
    }

}