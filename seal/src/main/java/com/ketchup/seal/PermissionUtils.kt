@file:Suppress("unused")

package com.ketchup.seal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ketchup.seal.callback.Callback
import com.ketchup.seal.callback.CallbackFragment
import com.ketchup.seal.callback.PermissionCallback

/**
 * 检测是否获取权限
 *
 * @param permission 权限名
 */
fun Context.checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.shouldRationale(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun Fragment.shouldRationale(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)

/**
 * 获取权限
 */
fun FragmentActivity.permission(vararg permission: String, block: PermissionCallback.() -> Unit) =
        requestPermission(permission.toList(), setup(block))

private fun FragmentActivity.requestPermission(permission: List<String>, callback: Callback) {

    val denied = permission.filter { !checkPermission(it) }

    if (denied.isEmpty()) callback.granted() else {

        val fragment = supportFragmentManager.findFragmentByTag(PERMISSION_FRAGMENT)

        if (fragment == null) {
            val frg = CallbackFragment()

            frg.apply {
                arguments = bundleOf(ASK_PERMISSIONS to denied as ArrayList<String>)
                callback(callback)
            }

            supportFragmentManager.beginTransaction().add(frg, PERMISSION_FRAGMENT)
                    .commitAllowingStateLoss()
        }
    }
}

private fun setup(callback: PermissionCallback.() -> Unit) =
        PermissionCallback().apply { callback() }

fun Context.startSetting() {
    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
    intent.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}