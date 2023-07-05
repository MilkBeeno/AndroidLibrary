package com.jetpack.androidlibrary.adapter

import android.widget.Toast
import com.example.common.viewHolder
import com.example.page.PagingHeaderAdapter
import com.jetpack.androidlibrary.databinding.ItemHeaderBinding

/**
 * Header 的第一种写法、创建类分离代码、减少 Activity 代码
 */
fun simpleHeaderAdapter() = PagingHeaderAdapter<ItemHeaderBinding>(
    // 创建 ViewHolder 反射原理
    create = { viewHolder() },
    // 处理点击事件区域
    clickScope = {
        binding.tvHeader.setOnClickListener {
            Toast.makeText(it.context, "你点击了 Simple 头部", Toast.LENGTH_SHORT).show()
        }
    },
    // 设置更新 UI 区域
    convert = {
        binding.tvHeader.text = "我是单类型头部"
    }
)
