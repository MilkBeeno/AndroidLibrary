package com.jetpack.androidlibrary.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.common.viewBinding
import com.jetpack.androidlibrary.adapter.SimpleTypeAdapter
import com.jetpack.androidlibrary.adapter.simpleFooterAdapter
import com.jetpack.androidlibrary.adapter.simpleHeaderAdapter
import com.jetpack.androidlibrary.databinding.ActivitySimpleTypeBinding
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

        binding.srl.setOnRefreshListener {
            simpleTypeAdapter.refresh()
        }

        // 提前设置 RecyclerView 的布局方式、不然无法添加头布局或尾布局
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // RecyclerView 头布局
        val headerAdapter = simpleHeaderAdapter()

        // RecyclerView 尾布局、可以是 LoadNoMoreData、LoadMoreError
        val footerAdapter = simpleFooterAdapter()

        // 初始化适配器、将创建 ViewHolder 和 BindViewHolder 给调用处处理
        simpleTypeAdapter = SimpleTypeAdapter({
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }) {
            binding.srl.isRefreshing = false
            binding.recyclerView.smoothScrollToPosition(0)
        }

        // 为 RecyclerView 设置适配器
        binding.recyclerView.adapter = simpleTypeAdapter
            .withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
        //.withLoadStateFooter(footerAdapter)

        // 通过观察请求网络数据或添加本地数据
        lifecycle.coroutineScope.launch {
            simpleTypeViewModel.pageWrapper.flow.collectLatest {
                simpleTypeAdapter.submitData(lifecycle, it)
            }
        }
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, SimpleTypeActivity::class.java))
    }
}