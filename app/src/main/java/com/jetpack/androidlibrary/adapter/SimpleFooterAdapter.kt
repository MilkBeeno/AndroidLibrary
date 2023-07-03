package com.jetpack.androidlibrary.adapter

import android.util.Log
import android.widget.Toast
import com.example.common.viewHolder
import com.example.page.AppendState
import com.example.page.PagingFooterAdapter
import com.jetpack.androidlibrary.databinding.ItemFooterBinding

class SimpleFooterAdapter : PagingFooterAdapter<ItemFooterBinding>(
    // 创建 ViewHolder 反射原理
    create = { viewHolder() },
    // 设置每页请求最大数量
    pageSize = 15,
    // 是否有 Header 影响计算自动加载更多阈值
    hasHeader = true,
    // 点击事件区域
    clickScope = {
        binding.tvFooter.setOnClickListener {
            Toast.makeText(it.context, "你点击了 Simple 尾部", Toast.LENGTH_SHORT).show()
        }
    },
    // 设置更新 UI 区域
    convert = {
        when (it) {
            AppendState.Loading -> {
                Log.d("hlc", "SimpleFooter Loading")
            }

            AppendState.Success -> {
                Log.d("hlc", "SimpleFooter Success")
            }

            AppendState.Error -> {
                Log.d("hlc", "SimpleFooter Error")
            }

            else -> Unit
        }
        binding.tvFooter.text = "我是单类型尾部"
    }
)