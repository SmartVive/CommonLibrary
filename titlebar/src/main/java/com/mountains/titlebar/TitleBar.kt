package com.mountains.titlebar

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.DrawableRes


class TitleBar : FrameLayout {
    var titleTextSize: Float
    var titleTextColor: Int
    var menuSize: Int
    var menuColor: Int
    var menuTextSize: Float
    var menuPadding: Int
    var menuPaddingLeft: Int
    var menuPaddingTop: Int
    var menuPaddingRight: Int
    var menuPaddingBottom: Int
    var menuMarginLeft:Int
    var menuMarginTop:Int
    var menuMarginRight:Int
    var menuMarginBottom:Int
    var searchViewMarginLeft: Int
    var searchViewMarginTop: Int
    var searchViewMarginRight: Int
    var searchViewMarginBottom: Int
    var searchViewPaddingLeft: Int
    var searchViewPaddingTop: Int
    var searchViewPaddingRight: Int
    var searchViewPaddingBottom: Int
    var searchViewTextSize: Float
    var titleMode = TITLE_MODE

    val searchView : EditText by lazy { initSearchView() }
    val titleView: TextView by lazy { initTitleView() }
    lateinit var leftMenuLayout: LinearLayout
        private set
    lateinit var rightMenuLayout: LinearLayout
        private set
    var ivBack: ImageView? = null

    companion object{
        //文字标题
        const val TITLE_MODE = 0
        //搜索框
        const val SEARCH_MODE = 1
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.TitleBar)
        titleTextSize = obtainStyledAttributes.getDimension(R.styleable.TitleBar_titleTextSize, sp2px(20f))
        titleTextColor = obtainStyledAttributes.getColor(R.styleable.TitleBar_titleTextColor, Color.WHITE)

        menuSize = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuSize, dp2px(36f)).toInt()
        menuColor = obtainStyledAttributes.getColor(R.styleable.TitleBar_menuColor, titleTextColor)
        menuTextSize = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuTextSize, sp2px(14f))

        menuMarginLeft = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuMarginLeft, dp2px(4f)).toInt()
        menuMarginTop = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuMarginTop, dp2px(0f)).toInt()
        menuMarginRight = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuMarginRight, dp2px(4f)).toInt()
        menuMarginBottom = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuMarginBottom, dp2px(0f)).toInt()

        menuPadding = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuPadding, dp2px(6f)).toInt()
        menuPaddingLeft = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuPaddingLeft, menuPadding.toFloat()).toInt()
        menuPaddingTop = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuPaddingTop, menuPadding.toFloat()).toInt()
        menuPaddingRight = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuPaddingRight, menuPadding.toFloat()).toInt()
        menuPaddingBottom = obtainStyledAttributes.getDimension(R.styleable.TitleBar_menuPaddingBottom, menuPadding.toFloat()).toInt()

        val defSearchViewMarginLeft = menuSize.toFloat() + menuPaddingLeft + menuPaddingRight
        searchViewMarginLeft = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewMarginLeft, defSearchViewMarginLeft).toInt()
        searchViewMarginTop = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewMarginTop, dp2px(8f)).toInt()
        searchViewMarginRight = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewMarginRight,defSearchViewMarginLeft).toInt()
        searchViewMarginBottom = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewMarginBottom, dp2px(8f)).toInt()

        searchViewPaddingLeft = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewPaddingLeft, dp2px(16f)).toInt()
        searchViewPaddingTop = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewPaddingTop, 0f).toInt()
        searchViewPaddingRight = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewPaddingRight, dp2px(16f)).toInt()
        searchViewPaddingBottom = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewPaddingBottom, 0f).toInt()

        searchViewTextSize = obtainStyledAttributes.getDimension(R.styleable.TitleBar_searchViewMTextSize, sp2px(14f))
        titleMode = obtainStyledAttributes.getInt(R.styleable.TitleBar_titleMode,TITLE_MODE)
        obtainStyledAttributes.recycle()
        init()
    }

    private fun init() {
        //initSearchView()
        //initTitleView()
        initLeftMenuLayout()
        initRightMenuLayout()
    }

    /**
     * 设置标题
     */
    fun setTitle(title: String?):TextView {
        hideSearch()
        titleView.text = title
        return titleView
    }

    /**
     * 隐藏标题
     */
    fun hideTitle() {
        titleView.visibility = View.GONE
    }

    /**
     * 显示标题
     */
    fun showTitle() {
        titleView.visibility = View.VISIBLE
    }

    /**
     * 显示搜索框
     */
    fun showSearch(){
        searchView.visibility = View.VISIBLE
    }

    /**
     * 隐藏索框
     */
    fun hideSearch(){
        searchView.visibility = View.GONE
    }

    /**
     * 设置搜索框
     */
    fun setSearch(hint: String, onSearchListener: OnSearchListener):EditText {
        hideTitle()
        searchView.hint = hint
        searchView.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchListener.onSearch(textView, searchView.text.toString())
            }
            return@setOnEditorActionListener true
        }
        //添加搜索按钮
        addRightIconMenu(R.drawable.titlebar_ic_search, OnClickListener {
            onSearchListener.onSearch(it, searchView.text.toString())
        })
        return searchView
    }

    /**
     * 显示返回键
     */
    fun showBack(listener: OnClickListener? = null) {
        if (ivBack == null) {
            ivBack = addLeftIconMenu(R.drawable.titlebar_ic_back,listener,0)
        } else {
            ivBack?.visibility = View.VISIBLE
        }

    }

    /**
     * 隐藏返回键
     */
    fun hideBack() {
        ivBack?.visibility = View.GONE
    }

    /**
     * 添加左侧文字菜单
     */
    fun addLeftTextMenu(text: String, listener: OnClickListener? = null):TextView = addLeftTextMenu(text, listener,-1)
    fun addLeftTextMenu(text: String, listener: OnClickListener? = null, index: Int = -1):TextView {
        val textMenu = createTextMenu(text, listener)
        leftMenuLayout.addView(textMenu, index)
        return textMenu
    }



    /**
     * 添加左侧文字菜单
     */
    fun addLeftIconMenu(@DrawableRes drawable: Int, listener: OnClickListener? = null):ImageView = addLeftIconMenu(drawable, listener,-1)
    fun addLeftIconMenu(
        @DrawableRes drawable: Int, listener: OnClickListener? = null,
        index: Int = -1
    ):ImageView {
        val iconMenu = createIconMenu(drawable, listener)
        leftMenuLayout.addView(iconMenu, index)
        return iconMenu
    }


    /**
     * 添加左侧自定义菜单
     */
    fun addLeftViewMenu(view: View):View = addLeftViewMenu(view,-1)
    fun addLeftViewMenu(view: View, index: Int = -1):View {
        leftMenuLayout.addView(view, index)
        return view
    }


    /**
     * 添加右侧文字菜单
     */
    fun addRightTextMenu(text: String, listener: OnClickListener? = null):TextView  = addRightTextMenu(text, listener,-1)
    fun addRightTextMenu(text: String, listener: OnClickListener? = null, index: Int = -1):TextView {
        val textMenu = createTextMenu(text, listener)
        rightMenuLayout.addView(textMenu, index)
        return textMenu
    }


    /**
     * 添加右侧图标菜单
     */
    fun addRightIconMenu(@DrawableRes drawable: Int, listener: OnClickListener? = null):ImageView = addRightIconMenu(drawable, listener,-1)
    fun addRightIconMenu(
        @DrawableRes drawable: Int, listener: OnClickListener? = null,
        index: Int = -1
    ):ImageView {
        val iconMenu = createIconMenu(drawable, listener)
        rightMenuLayout.addView(iconMenu, index)
        return iconMenu
    }


    /**
     * 添加右侧自定义菜单
     */
    fun addRightViewMenu(view: View)  = addRightViewMenu(view,-1)
    fun addRightViewMenu(view: View, index: Int = -1) {
        rightMenuLayout.addView(view, index)
    }

    /**
     * 创建图标菜单
     */
    private fun createIconMenu(@DrawableRes drawable: Int,listener: OnClickListener?):ImageView{
        val iconMenu = ImageView(context)
        iconMenu.setImageResource(drawable)
        iconMenu.setColorFilter(menuColor)
        val layoutParams = LayoutParams(menuSize, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(menuMarginLeft,menuMarginTop,menuMarginRight,menuMarginBottom)
        iconMenu.layoutParams = layoutParams
        iconMenu.setPadding(menuPaddingLeft, menuPaddingTop, menuPaddingRight, menuPaddingBottom)
        iconMenu.setOnClickListener(listener)
        setMenuBackground(iconMenu)
        return iconMenu
    }

    /**
     * 创建文字菜单
     */
    private fun createTextMenu(text: String, listener: OnClickListener? = null):TextView{
        val textMenu = TextView(context)
        textMenu.text = text
        textMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize)
        textMenu.setTextColor(menuColor)
        textMenu.gravity = Gravity.CENTER
        textMenu.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textMenu.setPadding(menuPaddingLeft, menuPaddingTop, menuPaddingRight, menuPaddingBottom)
        textMenu.setOnClickListener(listener)
        setMenuBackground(textMenu)
        return textMenu
    }



    /**
     * 初始化标题控件
     */
    private fun initTitleView():TextView {
        val titleView = TextView(context)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity  = Gravity.CENTER
        layoutParams.setMargins(2*menuSize,0,2*menuSize,0)
        titleView.layoutParams = layoutParams
        titleView.gravity = Gravity.CENTER
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
        titleView.setTextColor(titleTextColor)
        titleView.setLines(1)
        titleView.ellipsize = TextUtils.TruncateAt.END
        addView(titleView)
        return titleView
    }

    /**
     * 初始化搜索框控件
     */
    private fun initSearchView():EditText{
        val searchView = EditText(context)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
                searchViewMarginLeft,
                searchViewMarginTop,
                searchViewMarginRight,
                searchViewMarginBottom
        )
        searchView.layoutParams = layoutParams
        searchView.setPadding(
                searchViewPaddingLeft,
                searchViewPaddingTop,
                searchViewPaddingRight,
                searchViewPaddingBottom
        )
        searchView.setBackgroundResource(R.drawable.titlebar_bg_search_view)
        searchView.gravity = Gravity.CENTER
        searchView.setLines(1)
        searchView.setTextSize(TypedValue.COMPLEX_UNIT_PX, searchViewTextSize)
        searchView.inputType = InputType.TYPE_CLASS_TEXT
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        addView(searchView)
        return searchView
    }

    /**
     * 初始化左侧菜单控件
     */
    private fun initLeftMenuLayout() {
        leftMenuLayout = LinearLayout(context)
        leftMenuLayout.layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        leftMenuLayout.orientation = LinearLayout.HORIZONTAL
        addView(leftMenuLayout)
    }

    /**
     * 初始化右侧菜单控件
     */
    private fun initRightMenuLayout() {
        rightMenuLayout = LinearLayout(context)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.END
        rightMenuLayout.gravity = Gravity.CENTER
        rightMenuLayout.layoutParams = layoutParams
        rightMenuLayout.orientation = LinearLayout.HORIZONTAL
        addView(rightMenuLayout)
    }


    /**
     * 设置菜单点击效果
     */
    private fun setMenuBackground(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                typedValue,
                true
            )
            val attribute = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
            val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)
            view.background = typedArray.getDrawable(0)
        }
    }

    /**
     * dp转px
     */
    private fun dp2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * sp转px
     */
    private fun sp2px(spVal: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spVal * fontScale + 0.5f)
    }


    interface OnSearchListener{
       fun onSearch(view:View,searchText:String)
    }
}