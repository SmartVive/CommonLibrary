package com.mountains.chatinput.menu

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaRecorder
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mountains.chatinput.R
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object RecordUtil {
    private const val TAG = "RecordFragment"
    private val RECORD_PERMISSION = arrayOf(Manifest.permission.RECORD_AUDIO)
    private const val RECORD_PERMISSION_REQUEST_CODE = 100
    //更新音量
    private const val UPDATE_VOICE_LEVEL_MESSAGE = 101
    private var dialog: AlertDialog? = null
    private var ivVoiceLeave: ImageView? = null
    private var ivRecord: ImageView? = null
    private var tvHint: TextView? = null

    private var mediaRecorder: MediaRecorder? = null
    //音频保存地址
    private var recordSaveFile: File? = null
    //录制点击y坐标
    private var downY = 0f
    //是否正在录制
    private var isRecord = false
    //是否取消
    private var isCancel: Boolean = false
    //开始录制时间
    private var recordStartTime: Long = 0
    //滑动取消距离
    private var cancelDistance = 192
    //录音结果监听
    var recordResultListener: OnRecordResultListener? = null


    private fun checkPermission(context: Context): Boolean {
        for (permission in RECORD_PERMISSION) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestPermission(activity: FragmentActivity) {
        getFragment(activity).requestPermission()
    }

    fun setupRecordView(activity: FragmentActivity, view: View) {
        cancelDistance = ViewConfiguration.get(activity).scaledTouchSlop * 10
        view.setOnTouchListener { _, motionEvent ->
            if (!checkPermission(
                    activity.applicationContext
                )
            ) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    //没有权限去申请
                    requestPermission(
                        activity
                    )
                }
                return@setOnTouchListener true
            }

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    downY = motionEvent.rawY
                    //显示录音窗口
                    showRecordDialog(
                        activity
                    )
                    //开始录音
                    startRecord(activity.applicationContext)
                    recordStartTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    val y: Float = motionEvent.rawY
                    if (downY - y > cancelDistance) {
                        showCancelDialog()
                    } else {
                        showNormalDialog()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val time = System.currentTimeMillis() - recordStartTime
                    if (time < 1000) {
                        if(!isCancel){
                            //显示时间过短提示窗口
                            showVoiceTooShortDialog()
                        }
                        //关闭录音窗口
                        closeRecordDialog(
                            1000
                        )
                        //停止录音
                        stopRecord(1000)
                    } else {
                        //关闭录音窗口
                        closeRecordDialog()
                        //停止录音
                        stopRecord()
                        //语音消息长度
                        val duration =
                            ((System.currentTimeMillis() - recordStartTime) / 1000).toInt()
                        if (!isCancel && recordSaveFile != null) {
                            recordResultListener?.onRecordResult(
                                recordSaveFile!!, duration)
                        }
                    }
                }
            }
            return@setOnTouchListener true
        }
    }


    private fun startRecord(context: Context) {
        if (!isRecord){
            try {
                recordSaveFile =
                    createRecordFile(
                        context
                    )
                mediaRecorder = MediaRecorder()
                mediaRecorder?.setOutputFile(
                    recordSaveFile?.absolutePath)
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                isRecord = true
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "录制音频失败", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun stopRecord(delay: Long = 0) {
        if (isRecord) {
            Handler().postDelayed({
                try {
                    mediaRecorder?.stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isRecord = false
            }, delay)
        }

    }

    /**
     * 释放资源
     */
    fun release() {
        dialog?.dismiss()
        ivRecord = null
        ivVoiceLeave = null
        tvHint = null
        dialog = null
        mediaRecorder?.reset()
        mediaRecorder?.release()
        mediaRecorder = null
        recordResultListener = null
        handler.removeCallbacksAndMessages(null)
    }

    //创建路径
    private fun createRecordFile(context: Context): File {
        val audioRecorder = File(context.filesDir, "record")
        if (!audioRecorder.exists()) {
            audioRecorder.mkdirs()
        }
        val recordName =
            SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date()) + ".aac"
        return File(audioRecorder, recordName)
    }


    /**
     * 显示录音窗口
     */
    private fun showRecordDialog(context: FragmentActivity) {
        initRecordDialogIfNeed(context)
        showNormalDialog()
        dialog?.show()
        updateVoiceLevel()
    }


    /**
     * 关闭录音窗口
     */
    private fun closeRecordDialog(delay: Long = 0) {
        stopUpdateVoiceLevel()
        Handler().postDelayed({
            dialog?.dismiss()
        }, delay)
    }

    /**
     * 正常录音窗口
     */
    private fun showNormalDialog() {
        isCancel = false
        ivVoiceLeave?.visibility = View.VISIBLE
        ivRecord?.setImageResource(
            R.drawable.chatinput_ic_microphone
        )
        tvHint?.text = "手指上滑，取消发送"
        tvHint?.background = ColorDrawable(Color.TRANSPARENT)
    }

    /**
     * 上划取消窗口
     */
    private fun showCancelDialog() {
        isCancel = true
        if (ivVoiceLeave != null) {
            ivVoiceLeave!!.visibility = View.GONE
        }
        if (ivRecord != null) {
            ivRecord!!.setImageResource(
                R.drawable.chatinput_ic_chat_record_cancel
            )
        }
        if (tvHint != null) {
            tvHint!!.text = "松开手指，取消发送"
            tvHint!!.background = ColorDrawable(Color.parseColor("#913331"))
        }
    }


    /**
     * 录音时间太短
     */
    private fun showVoiceTooShortDialog() {
        ivVoiceLeave?.visibility = View.GONE
        ivRecord?.setImageResource(
            R.drawable.chatinput_ic_chat_voice_too_short
        )
        tvHint?.text = "录音时间太短"
        tvHint?.background = ColorDrawable(Color.TRANSPARENT)
    }

    //初始化dialog
    private fun initRecordDialogIfNeed(context: Context) {
        if (dialog == null) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.chatinput_dialog_chat_record, null)
            dialog = AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .show()
            val window = dialog?.window
            window?.setDimAmount(0f)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            ivVoiceLeave = view.findViewById<ImageView>(
                R.id.iv_voiceLeave
            )
            ivRecord = view.findViewById<ImageView>(
                R.id.iv_record
            )
            tvHint = view.findViewById<TextView>(
                R.id.tv_hint
            )
        }
    }

    private val handler = Handler(Handler.Callback { message ->
        try {
            val MAX_VOICE = 32767
            if (message.what == UPDATE_VOICE_LEVEL_MESSAGE && mediaRecorder != null) { //更新音量大小提示
                val maxAmplitude = mediaRecorder!!.maxAmplitude
                when {
                    maxAmplitude < MAX_VOICE / 7 -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v1
                        )
                    }
                    maxAmplitude < MAX_VOICE / 6 -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v2
                        )
                    }
                    maxAmplitude < MAX_VOICE / 5 -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v3
                        )
                    }
                    maxAmplitude < MAX_VOICE / 4 -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v4
                        )
                    }
                    maxAmplitude < MAX_VOICE / 3 -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v5
                        )
                    }
                    maxAmplitude < MAX_VOICE / 2 -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v6
                        )
                    }
                    maxAmplitude < MAX_VOICE -> {
                        ivVoiceLeave?.setImageResource(
                            R.drawable.chatinput_voice_level_v7
                        )
                    }
                }
                updateVoiceLevel(200)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        false
    })

    private fun updateVoiceLevel(delay: Long = 0) {
        handler.sendEmptyMessageDelayed(
            UPDATE_VOICE_LEVEL_MESSAGE, delay)
    }

    private fun stopUpdateVoiceLevel() {
        handler.removeMessages(
            UPDATE_VOICE_LEVEL_MESSAGE
        )
    }

    private fun getFragment(activity: FragmentActivity): RecordFragment {
        var recordFragment = activity.supportFragmentManager.findFragmentByTag(TAG)
        if (recordFragment != null) {//重复利用之前创建的
            (recordFragment as RecordFragment)
        } else {
            recordFragment =
                RecordFragment()
            activity.supportFragmentManager.beginTransaction().add(recordFragment,
                TAG
            ).commitNow()
        }
        return recordFragment
    }

    class RecordFragment : Fragment() {
        fun requestPermission() {
            requestPermissions(
                RECORD_PERMISSION,
                RECORD_PERMISSION_REQUEST_CODE
            )
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == RECORD_PERMISSION_REQUEST_CODE) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "未获取到所需权限，请开启权限", Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
        }
    }

    interface OnRecordResultListener {
        fun onRecordResult(file: File, duration: Int)
    }
}