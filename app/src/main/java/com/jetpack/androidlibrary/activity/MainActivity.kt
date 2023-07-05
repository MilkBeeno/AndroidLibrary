package com.jetpack.androidlibrary.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.common.viewBinding
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
            NestedScrollViewActivity.create(this)
        }
    }
}