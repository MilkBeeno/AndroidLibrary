package com.example.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * 简单权限请求库、只适配 Androidx 包下的 Context、使用 Fragment 进行回调事件。
 *
 * @param permissionConfig 请求权限配置信息
 */
class PermissionRequest private constructor(private val permissionConfig: PermissionConfig) {

    init {
        if (permissionDenied()) {
            val fragment = PermissionFragment.newInstance(permissionConfig)
            fragment.attachToActivity()
        } else {
            permissionConfig.requestSuccess?.invoke()
        }
    }

    // 如果有权限被拒绝则需要进行权限申请、如果没有的话直接全部权限授予成功
    private fun permissionDenied(): Boolean {
        val context = permissionConfig.activity ?: return false
        permissionConfig.permissions.forEach {
            val result = ContextCompat.checkSelfPermission(context, it)
            if (result == PackageManager.PERMISSION_DENIED) return true
        }
        return false
    }

    class Builder {
        // 权限请求配置信息
        private val permissionConfig = PermissionConfig()

        fun with(activity: FragmentActivity) = apply {
            permissionConfig.activity = activity
        }

        fun setForceAllPermissionsGranted(forceAll: Boolean) = apply {
            permissionConfig.isForceAllPermissionsGranted = forceAll
        }

        fun addPermission(permission: String) = apply {
            if (!permissionConfig.permissions.contains(permission)) {
                permissionConfig.permissions.add(permission)
            }
        }

        fun addPermissions(vararg permissions: String) = apply {
            permissions.forEach { addPermission(it) }
        }

        fun addARequestSuccessListener(listener: () -> Unit) = apply {
            permissionConfig.requestSuccess = listener
        }

        fun addRequestForceAllFailureListener(listener: (denied: List<String>) -> Unit) = apply {
            permissionConfig.requestForceAllFailure = listener
        }

        fun addRequestFailureListener(listener: (granted: List<String>, denied: List<String>, forceDenied: List<String>) -> Unit) = apply {
            permissionConfig.requestFailure = listener
        }

        // 创建 PermissionRequest
        fun build(): PermissionRequest {
            return PermissionRequest(permissionConfig)
        }
    }

    companion object {
        // 得到当前 App Name
        fun getAppName(context: Context): String {
            try {
                val pm = context.packageManager
                val packageInfo = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0L))
                } else {
                    pm.getPackageInfo(context.packageName, 0)
                }
                val applicationInfo = packageInfo.applicationInfo
                val labelRes = applicationInfo.labelRes
                return context.resources.getString(labelRes)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }
    }
}