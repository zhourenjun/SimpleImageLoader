package com.lh.zrj.simpleimageloader

import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog

/**
 * 6.0 运行时权限处理工具类。
 */
object MPermissionUtils {
    private var mRequestCode = -1
    var settingRequestCode = 1
    fun requestPermissionsResult(activity: Activity, requestCode: Int, permission: Array<String>,
                                 callback: OnPermissionListener) {
        requestPermissions(activity, requestCode, permission, callback)
    }

    /**
     * 请求权限处理
     * @param obj       activity or fragment
     * @param requestCode   请求码
     * @param permissions   需要请求的权限
     * @param callback      结果回调
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissions(obj: Any, requestCode: Int, permissions: Array<String>,
                                   callback: OnPermissionListener) {

        checkCallingObjectSuitability(obj)
        mOnPermissionListener = callback

        if (checkPermissions(getContext(obj), *permissions)) {
                mOnPermissionListener.onPermissionGranted()
        } else {
            val deniedPermissions = getDeniedPermissions(getContext(obj), *permissions)
            if (deniedPermissions.isNotEmpty()) {
                mRequestCode = requestCode
                when (obj) {
                    is Activity -> obj.requestPermissions(deniedPermissions.toTypedArray(), requestCode)
                    is android.app.Fragment -> obj.requestPermissions(deniedPermissions.toTypedArray(), requestCode)
                    is android.support.v4.app.Fragment -> obj.requestPermissions(deniedPermissions.toTypedArray(), requestCode)
                    else -> mRequestCode = -1
                }
            }
        }
    }

    /**
     * 获取上下文
     */
    private fun getContext(obj: Any): Context {
        return when (obj) {
            is Fragment -> obj.activity
            is android.support.v4.app.Fragment -> obj.activity!!
            else -> obj as Activity
        }
    }

    /**
     * 请求权限结果，对应onRequestPermissionsResult()方法。
     */
    fun onRequestPermissionsResult(requestCode: Int,  grantResults: IntArray) {
        if (mRequestCode != -1 && requestCode == mRequestCode) {
            if (verifyPermissions(grantResults)) {
                    mOnPermissionListener.onPermissionGranted()
            } else {
                    mOnPermissionListener.onPermissionDenied()
            }
        }
    }

    /**
     * 显示提示对话框
     */
    fun showTipsDialog(activity: Activity) {
        AlertDialog.Builder(activity)
                .setTitle("hint")
                .setMessage("The current application is missing the necessary permissions, which are temporarily unavailable. If necessary, click the [OK] button to go to the Settings Center for authorization.")
                .setNegativeButton(activity.getString(android.R.string.cancel), null)
                .setPositiveButton(activity.getString(android.R.string.ok), { _: DialogInterface, _: Int -> startAppSettings(activity) }
                ).show()
    }

    /**
     * 启动当前应用设置页面
     */
    private fun startAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + activity.packageName)
        activity.startActivityForResult(intent, settingRequestCode)
    }

    /**
     * 验证权限是否都已经授权
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        return grantResults.none { it != PackageManager.PERMISSION_GRANTED }
    }

    /**
     * 获取权限列表中所有需要授权的权限
     * @param context       上下文
     * @param permissions   权限列表
     * @return
     */
    private fun getDeniedPermissions(context: Context, vararg permissions: String): List<String> {
        return permissions.filter {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    /**
     * 检查所传递对象的正确性
     * @param obj 必须为 activity or fragment
     */
    private fun checkCallingObjectSuitability(obj: Any?) {
        if (obj == null) {
            throw NullPointerException("Activity or Fragment should not be null")
        }

        val isActivity = obj is Activity
        val isSupportFragment = obj is android.support.v4.app.Fragment
        val isAppFragment = obj is android.app.Fragment

        if (!(isActivity || isSupportFragment || isAppFragment)) {
            throw IllegalArgumentException("Caller must be an Activity or a Fragment")
        }
    }

    /**
     * 检查所有的权限是否已经被授权
     * @param permissions 权限列表
     * @return
     */
    private fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        if (isOverMarshmallow()) {
            permissions.filter {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
                    .forEach { return false }
        }
        return true
    }

    /**
     * 判断当前手机API版本是否 >= 6.0
     */
    private fun isOverMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }


    interface OnPermissionListener {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }

    private lateinit var mOnPermissionListener: OnPermissionListener
}
