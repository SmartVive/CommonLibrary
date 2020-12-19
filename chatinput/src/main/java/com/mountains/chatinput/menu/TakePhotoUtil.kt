package com.mountains.chatinput.menu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mountains.chatinput.ChatInputView
import java.io.File

object TakePhotoUtil {
    private const val TAG = "TakePhotoFragment"
    var listener: OnTakePhotoResultListener? = null

    interface OnTakePhotoResultListener{
        fun onTakePhotoResult(result:MutableList<String>)
    }

    fun takePhoto(activity: FragmentActivity, listener: OnTakePhotoResultListener){
        //利用fragment的activityResult获取选择结果
        TakePhotoUtil.listener = listener
        getFragment(activity).takePhoto()
    }

    private fun getFragment(activity: FragmentActivity): TakePhotoFragment {
        var takePhotoFragment = activity.supportFragmentManager.findFragmentByTag(TAG)
        if(takePhotoFragment != null){//重复利用之前创建的
            (takePhotoFragment as TakePhotoFragment)
        }else{
            takePhotoFragment =
                TakePhotoFragment()
            activity.supportFragmentManager.beginTransaction().add(takePhotoFragment,
                TAG
            ).commitNow()
        }
        return takePhotoFragment
    }

    class TakePhotoFragment: Fragment(){
        //相机相片保存路径
        private var photoSaveFile: File? = null
        companion object{
            private const val TAKE_PHOTO_REQUEST_CODE = 100
            private const val TAKE_PHOTO_PERMISSION_REQUEST_CODE = 101
            private val TAKE_PHOTO_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
        }

        fun takePhoto(){
            requestPermissions(
                TAKE_PHOTO_PERMISSION,
                TAKE_PHOTO_PERMISSION_REQUEST_CODE
            )
        }

        private fun realTakePhoto(){
            val picturesFile:String
            val authority:String
            if (ChatInputView.captureStrategy != null){ //配置了保存路径
                picturesFile =ChatInputView.captureStrategy!!.directory
                authority = ChatInputView.captureStrategy!!.authority
            }else{//默认保存路径
                picturesFile = requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath ?: return
                authority = requireContext().packageName+".chatinput.provider"
            }

            photoSaveFile = File(picturesFile, System.currentTimeMillis().toString() + ".jpg")
            Log.d(javaClass.simpleName, "photoSaveFile" + photoSaveFile?.path)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //判断版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //如果在Android7.0以上,使用FileProvider获取Uri
                val contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    authority,
                    photoSaveFile!!
                )
                intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            } else { //否则使用Uri.fromFile(file)方法获取Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoSaveFile))
            }
            //调用系统相机
            startActivityForResult(
                intent,
                TAKE_PHOTO_REQUEST_CODE
            )
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode == TAKE_PHOTO_PERMISSION_REQUEST_CODE){
                for (grantResult in grantResults) {
                    if(grantResult != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(context,"未获取到所需权限，请开启权限",Toast.LENGTH_LONG).show()
                        return
                    }
                }
                realTakePhoto()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            //拍照
            if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                photoSaveFile?.let {
                    if (it.exists()) {
                        Log.d(javaClass.simpleName,"resultPath:${it.path}")
                        val paths =arrayListOf<String>(it.absolutePath)
                        listener?.onTakePhotoResult(paths)
                    }
                }

            }

        }
    }
}