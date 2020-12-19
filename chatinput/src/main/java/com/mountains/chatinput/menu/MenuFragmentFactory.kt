package com.mountains.chatinput.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mountains.chatinput.R
import com.mountains.chatinput.entity.MenuItem
import kotlinx.android.synthetic.main.chatinput_fragment_menu.*

object MenuFragmentFactory {
    fun create(menuItemList: List<MenuItem>,menuColumns:Int): MenuFragment {
        return MenuFragment().apply {
            this.menuItemList = menuItemList
            this.menuColumns = menuColumns
        }
    }

    class MenuFragment : Fragment() {
        lateinit var menuItemList:List<MenuItem>
        var menuColumns:Int = 4
        var menuClickListener: OnMenuClickListener? =null

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.chatinput_fragment_menu,container,false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            gridView.numColumns = menuColumns
            gridView.adapter = object : BaseAdapter(){
                override fun getView( position: Int, convertView: View?, parent: ViewGroup?): View {
                    val viewHolder: ViewHolder;
                    val inflate:View
                    if(convertView == null){
                        viewHolder =
                            ViewHolder()
                        inflate = LayoutInflater.from(context).inflate(R.layout.chatinput_layout_menu_item, parent, false)
                        viewHolder.ivIcon = inflate.findViewById<ImageView>(R.id.ivIcon)
                        viewHolder.tvLabel = inflate.findViewById<TextView>(R.id.tvLabel)
                        inflate.tag = viewHolder
                    }else{
                        viewHolder = convertView.tag as ViewHolder
                        inflate = convertView
                    }

                    if(menuItemList.size>position){
                        inflate.visibility = View.VISIBLE
                        viewHolder.ivIcon?.setImageResource(getItem(position).icon)
                        viewHolder.tvLabel?.text = getItem(position).label
                    }else{
                        inflate.visibility = View.GONE
                    }

                    return inflate
                }

                override fun getItem(position: Int) = menuItemList[position]

                override fun getItemId(position: Int) = position.toLong()

                override fun getCount() = 8

            }

            gridView.setOnItemClickListener { parent, view, position, id ->
                if(menuItemList.size>position){
                    menuClickListener?.onMenuClick(menuItemList[position])
                }
            }


        }

        class ViewHolder{
            var ivIcon:ImageView? = null
            var tvLabel:TextView? = null
        }

        interface OnMenuClickListener{
            fun onMenuClick(menuItem: MenuItem)
        }

    }
}