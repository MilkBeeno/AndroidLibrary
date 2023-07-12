package com.jetpack.androidlibrary.activity

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.common.viewBinding
import com.example.permission.PermissionRequest
import com.jetpack.androidlibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.simpleType.setOnClickListener {
            SingleTypeActivity.create(this)
        }
        binding.multipleType.setOnClickListener {
            MultipleTypeActivity.create(this)
        }
        binding.nestedView.setOnClickListener {
            // NestedScrollViewActivity.create(this)
            PermissionRequest.Builder()
                .with(this)
                .addPermission(Manifest.permission.CAMERA)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                //.addPermission(Manifest.permission.READ_MEDIA_IMAGES)
                //.addPermission(Manifest.permission.READ_MEDIA_VIDEO)
                .setForceAllPermissionsGranted(false)
                .addARequestSuccessListener {
                    Log.d("hlc", "所有权限申请成功")
                }
                .addRequestFailureListener { g, d, f ->
                    g.forEach {
                        Log.d("hlc", "----允许权限$it")
                    }
                    d.forEach {
                        Log.d("hlc", "拒绝权限$it-----")
                    }
                    f.forEach {
                        Log.d("hlc", "----不再提示权限$it----")
                    }
                }
                .addRequestForceAllFailureListener {
                    Log.d("hlc", "强制请求所有权限被拒绝")
                }
                .build()
        }
    }
}