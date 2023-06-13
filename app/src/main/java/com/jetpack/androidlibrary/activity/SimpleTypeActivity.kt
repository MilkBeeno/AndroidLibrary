package com.jetpack.androidlibrary.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.common.viewBinding
import com.example.common.viewHolder
import com.example.page.PageFooterAdapter
import com.example.page.PageHeaderAdapter
import com.example.page.SimplePagingDataAdapter
import com.jetpack.androidlibrary.adapter.SimpleTypeAdapter
import com.jetpack.androidlibrary.data.SimpleTypeModel
import com.jetpack.androidlibrary.databinding.ActivitySimpleTypeBinding
import com.jetpack.androidlibrary.databinding.ItemFooterBinding
import com.jetpack.androidlibrary.databinding.ItemHeaderBinding
import com.jetpack.androidlibrary.databinding.ItemUserBinding
import com.jetpack.androidlibrary.viewmodel.SimpleTypeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SimpleTypeActivity : AppCompatActivity() {
    // setContentView() viewBinding 实现
    private val binding: ActivitySimpleTypeBinding by viewBinding()

    // 代理实现 viewModel
    private val simpleTypeViewModel: SimpleTypeViewModel by viewModels()

    // 定义单 type 类型列表数据适配器
    private lateinit var simpleTypeAdapter: SimpleTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 提前设置 RecyclerView 的布局方式、不然无法添加头布局或尾布局
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        // RecyclerView 头布局
        val headerAdapter = PageHeaderAdapter(
            create = { viewHolder<ItemHeaderBinding>() },
            gestureScope = {
                tvHeader.setOnClickListener {
                    Toast.makeText(this@SimpleTypeActivity, "你点击了 Simple 头部", Toast.LENGTH_SHORT).show()
                }
            },
            convert = {
                it.binding.tvHeader.text = "我是单类型头部"
            })
        // RecyclerView 尾布局、可以是 LoadNoMoreData、LoadMoreError
        val footerAdapter = PageFooterAdapter(
            create = { viewHolder<ItemFooterBinding>() },
            pageSize = 15,
            hasHeader = true,
            gestureScope = {
                tvFooter.setOnClickListener {
                    Toast.makeText(this@SimpleTypeActivity, "你点击了 Simple 尾部", Toast.LENGTH_SHORT).show()
                }
            },
            convert = { _, viewHolder ->
                viewHolder.binding.tvFooter.text = "我是单类型尾部"
            })
        // 初始化适配器、将创建 ViewHolder 和 BindViewHolder 给调用处处理
        simpleTypeAdapter = SimpleTypeAdapter {
            Toast.makeText(this, "我点击的是listener第 $it 个位置回调", Toast.LENGTH_SHORT).show()
        }
        // 为 RecyclerView 设置适配器
        binding.recyclerView.adapter = simpleTypeAdapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
        // 通过观察请求网络数据或添加本地数据
        lifecycle.coroutineScope.launch {
            simpleTypeViewModel.pageWrapper.dataFlow.collectLatest {
                simpleTypeAdapter.submitData(it)
            }
        }
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, SimpleTypeActivity::class.java))
    }
}