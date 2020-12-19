package com.mountains.chatinput.menu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mountains.chatinput.util.GlideEngine
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType

object ChosePhotoUtil {
    private const val TAG = "chosePhotoFragment"
    var listener: OnChosePhotoResultListener? = null

    interface OnChosePhotoResultListener{
        fun onChosePhotoResult(result:MutableList<String>)
    }

    fun chosePhoto(activity: FragmentActivity,listener: OnChosePhotoResultListener){
        //利用fragment的activityResult获取选择结果
        ChosePhotoUtil.listener = listener
        getFragment(activity).chosePhoto()
    }

    private fun getFragment(activity: FragmentActivity): ChosePhotoFragment {
        var chosePhotoFragment = activity.supportFragmentManager.findFragmentByTag(
            TAG
        )
        if(chosePhotoFragment != null){//重复利用之前创建的
            (chosePhotoFragment as ChosePhotoFragment)
        }else{
            chosePhotoFragment =
                ChosePhotoFragment()
            activity.supportFragmentManager.beginTransaction().add(chosePhotoFragment,
                TAG
            ).commitNow()
        }
        return chosePhotoFragment
    }

    class ChosePhotoFragment: Fragment(){
        companion object{
            private const val CHOSE_PHOTO_REQUEST_CODE = 100
            private const val CHOSE_PHOTO_PERMISSION_REQUEST_CODE = 101
            private val CHOSE_PHOTO_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }


        fun chosePhoto(){
            requestPermissions(
                CHOSE_PHOTO_PERMISSION,
                CHOSE_PHOTO_PERMISSION_REQUEST_CODE
            )
        }

        private fun realChosePhoto(){
            Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9)
                .imageEngine(GlideEngine())
                .forResult(CHOSE_PHOTO_REQUEST_CODE)
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode == CHOSE_PHOTO_PERMISSION_REQUEST_CODE){
                for (grantResult in grantResults) {
                    if(grantResult != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(context,"未获取到所需权限，请开启权限",Toast.LENGTH_LONG).show()
                        return
                    }
                }
                realChosePhoto()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CHOSE_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val result = Matisse.obtainPathResult(data);
                Log.d("Matisse", "mSelected: " + result);
                listener?.onChosePhotoResult(result)
            }
        }
    }
}