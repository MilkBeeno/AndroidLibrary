package com.jetpack.androidlibrary.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.common.viewBinding
import com.example.common.viewHolder
import com.example.page.AppendState
import com.example.page.PagingFooterAdapter
import com.example.page.PagingHeaderAdapter
import com.example.page.RefreshState
import com.jetpack.androidlibrary.adapter.MultipleTypeAdapter
import com.jetpack.androidlibrary.databinding.ActivityMultipleTypeBinding
import com.jetpack.androidlibrary.databinding.ItemFooterBinding
import com.jetpack.androidlibrary.databinding.ItemHeaderBinding
import com.jetpack.androidlibrary.viewmodel.MultipleTypeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MultipleTypeActivity : AppCompatActivity() {
    // setContentView() viewBinding 实现
    private val binding: ActivityMultipleTypeBinding by viewBinding()

    // 代理实现 viewModel
    private val multipleTypeViewModel: MultipleTypeViewModel by viewModels()

    // 定义多 type 类型列表数据适配器
    private lateinit var multipleTypeAdapter: MultipleTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 提前设置 RecyclerView 的布局方式、不然无法添加头布局或尾布局
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // RecyclerView 头布局
        val headerAdapter = PagingHeaderAdapter(
            // 创建 ViewHolder
            create = { viewHolder<ItemHeaderBinding>() },
            // 点击事件区域
            clickScope = {
                binding.tvHeader.setOnClickListener {
                    Toast.makeText(this@MultipleTypeActivity, "你点击了 Multiple 头部", Toast.LENGTH_SHORT).show()
                }
            },
            // 更新 UI 区域
            convert = {
                when (it) {
                    RefreshState.Loading -> {
                        Log.d("hlc", "MultipleHeader Loading")
                    }

                    RefreshState.Success -> {
                        Log.d("hlc", "MultipleHeader Success")
                    }

                    RefreshState.Error -> {
                        Log.d("hlc", "MultipleHeader Error")
                    }

                    else -> Unit
                }
                binding.tvHeader.text = "我是多类型头部"
            })

        // RecyclerView 尾布局、可以是 LoadNoMoreData、LoadMoreError
        val footerAdapter = PagingFooterAdapter(
            // 创建 ViewHolder
            create = { viewHolder<ItemFooterBinding>() },
            // 设置每页数量
            pageSize = 15,
            // 是否含有头部
            hasHeader = true,
            // 点击事件区域
            clickScope = {
                binding.tvFooter.setOnClickListener {
                    Toast.makeText(this@MultipleTypeActivity, "你点击了 Multiple 尾部", Toast.LENGTH_SHORT).show()
                }
            },
            // 更新 UI 区域
            convert = {
                when (it) {
                    AppendState.Loading -> {
                        Log.d("hlc", "MultipleFooter Loading")
                    }

                    AppendState.Success -> {
                        Log.d("hlc", "MultipleFooter Success")
                    }

                    AppendState.Error -> {
                        Log.d("hlc", "MultipleFooter Error")
                    }

                    else -> Unit
                }
                binding.tvFooter.text = "我是多类型尾部"
            })

        // 初始化适配器、将创建 ViewHolder 和 BindViewHolder 给调用处处理
        multipleTypeAdapter = MultipleTypeAdapter {
            Toast.makeText(this, "我点击的是listener回调", Toast.LENGTH_SHORT).show()
        }

        // 为 RecyclerView 设置适配器
        binding.recyclerView.adapter = multipleTypeAdapter
            .withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        // 通过观察请求网络数据或添加本地数据
        lifecycle.coroutineScope.launch {
            multipleTypeViewModel.pageWrapper.flow.collectLatest {
                multipleTypeAdapter.submitData(it)
            }
        }
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, MultipleTypeActivity::class.java))
    }
}