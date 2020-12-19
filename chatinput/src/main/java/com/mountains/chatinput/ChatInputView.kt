package com.mountains.chatinput

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mountains.chatinput.entity.CaptureStrategy
import com.mountains.chatinput.entity.ChatInputConfig
import com.mountains.chatinput.entity.InputModel
import com.mountains.chatinput.entity.MenuItem
import com.mountains.chatinput.menu.*
import com.mountains.chatinput.menu.MenuFragmentFactory
import com.mountains.chatinput.util.*
import kotlinx.android.synthetic.main.chatinput_view_chat_input.view.*
import java.io.File


class ChatInputView : FrameLayout {

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.ChatInputView)
        val contentTextColor = typedArray.getColor(R.styleable.ChatInputView_contentTextColor, ContextCompat.getColor(context, R.color.defaultContentColor))
        val sendButtonBackground = typedArray.getResourceId(R.styleable.ChatInputView_sendButtonBackground,R.drawable.chatinput_bg_btn_chat_send)
        val dp320 = resources.displayMetrics.density*320+0.5f
        val menuLayoutHeight = typedArray.getDimension(R.styleable.ChatInputView_menuLayoutHeight,dp320).toInt()
        val isSupportVoiceMessage = typedArray.getBoolean(R.styleable.ChatInputView_isSupportVoiceMessage, true)
        typedArray.recycle()
        etContent.setTextColor(contentTextColor)
        btnSend.setBackgroundResource(sendButtonBackground)
        setMenuLayoutHeight(SharedUtil.read(context, KEYBOARD_HEIGHT_KEY,menuLayoutHeight))
        if(isSupportVoiceMessage){
            btnVoice.visibility = View.VISIBLE
        }else{
            btnVoice.visibility = View.GONE
        }
    }

    private val TAG = javaClass.simpleName
    private var keyboardStatePopupWindow: KeyboardStatePopupWindow? = null
    //当前模式
    private var currentInputModel =  InputModel.MODEL_NONE
    //菜单监听
    var menuItemListener:OnMenuItemClickListener? = null
    //信息监听
    var messageListener:OnMessageListener? = null
    //键盘状态监听
    var stateListener : OnStateListener? = null


    companion object{
        private const val KEYBOARD_HEIGHT_KEY = "keyboardHeightKey"
        private const val SHOW_MENU_MSG = 100

        var captureStrategy: CaptureStrategy? = null
    }

    private val handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what){
                SHOW_MENU_MSG->{
                    flMenu.visibility = View.VISIBLE
                }
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.chatinput_view_chat_input, this)
        initKeyBoardHeightListener()
        btnVoice.setOnClickListener {
            when(currentInputModel){
                InputModel.MODEL_VOICE->setInputModel(
                    InputModel.MODEL_TEXT)
                else->setInputModel(InputModel.MODEL_VOICE)
            }
        }

        btnMenu.setOnClickListener {
            when(currentInputModel){
                InputModel.MODEL_MENU->setInputModel(
                    InputModel.MODEL_TEXT)
                else->setInputModel(InputModel.MODEL_MENU)

            }
        }

        etContent.setOnClickListener {
            setInputModel(InputModel.MODEL_TEXT)
        }

        etContent.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty()) {
                    btnMenu.visibility = View.GONE
                    btnSend.visibility = View.VISIBLE
                } else {
                    btnMenu.visibility = View.VISIBLE
                    btnSend.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {    }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}

        })

        etContent.setOnKeyListener { view, i, keyEvent ->
            if(keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP && currentInputModel != InputModel.MODEL_NONE){
                setInputModel(InputModel.MODEL_NONE)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        //发送文字信息
        btnSend.setOnClickListener {
            messageListener?.sendTextMessage(etContent.text.toString())
            etContent.text.clear()
        }

        //录音
        RecordUtil.setupRecordView(context as FragmentActivity,btnRecord)
        RecordUtil.recordResultListener = object : RecordUtil.OnRecordResultListener{
            override fun onRecordResult(file: File, duration: Int) {
                Log.d(TAG,"filePath:${file.absolutePath},duration:$duration")
                messageListener?.sendVoiceMessage(file,duration)
            }

        }
    }

    /**
     * 初始化配置信息
     */
    fun initChatInputConfig(config: ChatInputConfig){
        initMenuItem(config.menuItemList,config.menuColumns)
        captureStrategy = config.captureStrategy
        config.contentTextColor?.let{
         etContent.setTextColor(it)
        }
        config.sendButtonBackground?.let{
            btnSend.setBackgroundResource(it)
        }
        config.menuLayoutHeight?.let{
            setMenuLayoutHeight(it)
        }
        if(config.isSupportVoiceMessage){
            btnVoice.visibility = View.VISIBLE
        }else{
            btnVoice.visibility = View.GONE
        }
    }

    private fun initMenuItem(menuItemList:List<MenuItem>,menuColumns:Int){
        /*var parent:ViewParent? = flMenu
        while(parent != null){
            (parent as ViewGroup).clipChildren = false
            parent = parent.parent
            Log.e(TAG,"${parent}")
        }*/



        val menuFragment = MenuFragmentFactory.create(menuItemList,menuColumns)
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMenu,menuFragment)
        transaction.commit()

        menuFragment.menuClickListener = object : MenuFragmentFactory.MenuFragment.OnMenuClickListener{
            override fun onMenuClick(menuItem: MenuItem) {
                when(menuItem.tag){
                    PresetMenuItem.TAG_PHOTO->{
                        ChosePhotoUtil.chosePhoto(context as FragmentActivity,object : ChosePhotoUtil.OnChosePhotoResultListener{
                            override fun onChosePhotoResult(result: MutableList<String>) {
                                menuItemListener?.sendImageMessage(result)
                            }
                        })
                    }
                    PresetMenuItem.TAG_CAMERA->{
                        TakePhotoUtil.takePhoto(context as FragmentActivity,object :
                            TakePhotoUtil.OnTakePhotoResultListener{
                            override fun onTakePhotoResult(result: MutableList<String>) {
                                menuItemListener?.sendImageMessage(result)
                            }
                        })
                    }
                    PresetMenuItem.TAG_CALL->{
                        CallUtil.showSelectCallTypePopup(this@ChatInputView,context){
                            Log.d(TAG,"callType:$it")
                            if(it == CallUtil.TYPE_VOICE_CALL){
                                menuItemListener?.voiceCall()
                            }else{
                                menuItemListener?.videoCall()
                            }
                        }
                    }
                    else->{
                        menuItemListener?.otherMenuItem(menuItem)
                    }

                }
            }
        }
    }


    fun setContentText(text:String){
        etContent.setText(text)
    }

    fun getContentText():String{
        return etContent.text.toString()
    }

    fun setInputModel(inputModel: InputModel){

        //防止菜单延迟显示导致的布局错乱
        handler.removeMessages(SHOW_MENU_MSG)

        when(inputModel){
            InputModel.MODEL_NONE->{
                flMenu.visibility = View.GONE
                etContent.visibility = View.VISIBLE
                btnRecord.visibility = View.GONE
                hideKeyboard()
                btnVoice.setImageResource(R.drawable.chatinput_ic_chat_voice)
            }
            InputModel.MODEL_TEXT->{
                flMenu.visibility = View.GONE
                btnRecord.visibility = View.GONE
                etContent.visibility = View.VISIBLE
                etContent.isCursorVisible = true
                showKeyboard()
                btnVoice.setImageResource(R.drawable.chatinput_ic_chat_voice)
            }
            InputModel.MODEL_VOICE->{
                flMenu.visibility = View.GONE
                btnRecord.visibility = View.VISIBLE
                etContent.visibility = View.GONE
                hideKeyboard()
                btnVoice.setImageResource(R.drawable.chatinput_ic_keyboard)
            }
            InputModel.MODEL_MENU->{
                btnRecord.visibility = View.GONE
                etContent.visibility = View.VISIBLE
                //防止进入Activity后直接按菜单再按返回无法调用etContent.KeyListener
                etContent.requestFocus()
                etContent.isCursorVisible = false
                hideKeyboard()
                btnVoice.setImageResource(R.drawable.chatinput_ic_chat_voice)
                handler.sendEmptyMessageDelayed(SHOW_MENU_MSG,100)
            }
        }
        currentInputModel = inputModel
        stateListener?.onInputModelChange(currentInputModel)
    }


    /**
     * 隐藏键盘
     */
    private fun hideKeyboard() {
        val imm = etContent.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etContent.windowToken, 0)
    }

    /**
     * 显示键盘
     */
    private fun showKeyboard() {
        etContent.requestFocus()
        val imm = etContent.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etContent,0)
    }

    fun setMenuLayoutHeight(height:Int){
        flMenu.layoutParams.height = height
    }

    /**
     * 初始化键盘高度监听
     */
    private fun initKeyBoardHeightListener(){
        keyboardStatePopupWindow =
            KeyboardStatePopupWindow(context, this)
        keyboardStatePopupWindow?.setOnKeyboardStateListener(object :
            KeyboardStatePopupWindow.OnKeyboardStateListener{
            override fun onOpened(keyboardHeight: Int) {
                setMenuLayoutHeight(keyboardHeight)
                SharedUtil.save(context,KEYBOARD_HEIGHT_KEY,keyboardHeight)
                stateListener?.onKeyBoardOpened(keyboardHeight)
            }

            override fun onClosed() {
                stateListener?.onKeyBoardClosed()
            }

        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //释放资源防止内存泄漏
        ChosePhotoUtil.listener = null
        TakePhotoUtil.listener = null
        RecordUtil.release()
        keyboardStatePopupWindow?.release()
        stateListener = null
        handler.removeCallbacksAndMessages(null)
    }


    abstract class OnMenuItemClickListener{
        open fun sendImageMessage(files:List<String>){}
        open fun voiceCall(){}
        open fun videoCall(){}
        open fun otherMenuItem(menuItem: MenuItem){}
    }

    interface OnMessageListener{
        fun sendTextMessage(text:String)
        fun sendVoiceMessage(file:File,duration:Int)
    }

    interface OnStateListener{
        fun onKeyBoardOpened(keyboardHeight:Int)
        fun onKeyBoardClosed()
        fun onInputModelChange(inputModel: InputModel)
    }
}