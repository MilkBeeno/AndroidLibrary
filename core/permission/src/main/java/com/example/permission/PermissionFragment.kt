package com.example.permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionFragment : Fragment() {

    companion object {

        private const val PERMISSION_CONFIG = "config"

        // 创建 PermissionFragment
        fun newInstance(permissionConfig: PermissionConfig): PermissionFragment {
            val args = Bundle()
            args.putParcelable(PERMISSION_CONFIG, permissionConfig)
            val fragment = PermissionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val config by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(PERMISSION_CONFIG, PermissionConfig::class.java)
        } else {
            arguments?.getParcelable(PERMISSION_CONFIG) as PermissionConfig?
        }
    }

    private val permissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        // 记录点击了不再提醒的未授权权限
        val forceDeniedPermissions = mutableListOf<String>()
        // 记录点击了普通的未授权权限
        val normalDeniedPermissions = mutableListOf<String>()
        // 记录授权通过的权限
        val grantedPermissions = mutableListOf<String>()
        val context = checkNotNull(config?.activity)

        // 权限是否请求成功的一个判断
        config?.permissions?.forEach { permission ->
            val grantResult = ContextCompat.checkSelfPermission(context, permission)
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permission)
            } else {
                // 1.第一次请求权限时点击空白处返回 false
                // 2.第一次请求权限拒绝，但未选中【不再提醒】返回 true
                // 3.允许某项权限后返回 false
                // 4.禁止权限并选中【禁止后不再询问】返回 false
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                    normalDeniedPermissions.add(permission)
                } else {
                    forceDeniedPermissions.add(permission)
                }
            }
        }

        when {
            // 申请的权限全部通过
            forceDeniedPermissions.isEmpty() && normalDeniedPermissions.isEmpty() -> {
                requestPermissionsSuccess()
            }
            // 强制需要通过所有的权限、还有普通未授权的让用户一直去请求
            config?.isForceAllPermissionsGranted == true && normalDeniedPermissions.isEmpty() -> {
                requestPermission()
            }
            // 强制权限已经设置不再提示、点击回调出去
            config?.isForceAllPermissionsGranted == true -> {
                config?.requestForceAllFailure?.invoke(forceDeniedPermissions)
            }
            // 未获得所有权限
            else -> {
                requestPermissionFailure(grantedPermissions, normalDeniedPermissions, forceDeniedPermissions)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun attachToActivity() {
        if (Looper.getMainLooper() != Looper.myLooper()) return
        // 普通版本可能已经适配了 SDK >= 24
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            config?.requestSuccess?.invoke()
        } else {
            // 将透明 Fragment 添加到 FragmentActivity、add() 函数 tag 是 Nullable 的
            config?.activity?.supportFragmentManager?.beginTransaction()?.add(this, activity?.javaClass?.name)?.commitAllowingStateLoss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermission()
    }

    private fun requestPermission() {
        // 授权未通过列表集合
        val deniedPermissions = mutableListOf<String>()
        config?.permissions?.forEach {
            val granted = ContextCompat.checkSelfPermission(requireContext(), it)
            if (granted == PackageManager.PERMISSION_GRANTED) {
                // Permission has granted do nothing there.
            } else {
                deniedPermissions.add(it)
            }
        }
        // 发起权限请求
        if (deniedPermissions.isNotEmpty()) {
            permissionResult.launch(deniedPermissions.toTypedArray())
        } else {
            requestPermissionsSuccess()
        }
    }

    private fun requestPermissionsSuccess() {
        config?.requestSuccess?.invoke()
        config?.activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

    private fun requestPermissionFailure(grantedPermissions: List<String>, deniedPermissions: List<String>, forceDeniedPermissions: List<String>) {
        config?.requestFailure?.invoke(grantedPermissions, deniedPermissions, forceDeniedPermissions)
        config?.activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }
}