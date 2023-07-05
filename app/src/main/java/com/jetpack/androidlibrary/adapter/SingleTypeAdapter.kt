package com.jetpack.androidlibrary.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.common.viewHolder
import com.example.page.AppendState
import com.example.page.RefreshState
import com.example.page.SinglePagingDataAdapter
import com.jetpack.androidlibrary.R
import com.jetpack.androidlibrary.data.SingleTypeModel
import com.jetpack.androidlibrary.databinding.ItemUserBinding

/**
 * 单个列表适配器模版
 *
 * @property listener 表示子项中单个手势事件回调结果、按单个创建不同回调给 Activity
 */
class SingleTypeAdapter(
    // 创建单个手势事件回调、细分每个子项
    private val listener: (String) -> Unit,
    private var refresh: () -> Unit
) : SinglePagingDataAdapter<SingleTypeModel, ItemUserBinding>(
    // 是否有头部影响刷新和加载更多状态
    hasHeader = true,
    // 创建 viewHolder
    create = { viewHolder() },
    // 对 itemView 进行数据绑定
    convert = { data, _ ->
        binding.ivUserAvatar.setImageResource(R.drawable.ic_launcher_background)
        binding.tvUserName.text = data.name
        binding.tvUserDescribe.text = data.describe
    },
    // 点击事件区域
    clickScope = { adapter ->
        binding.root.setOnClickListener {
            // 因为在 RecyclerView 中添加了头部 Adapter 所以 absoluteAdapterPosition 的位置 index 是不正确的应当减 1
            val itemModel = adapter.getNotNullItem(absoluteAdapterPosition - 1)
            listener(itemModel.name)
        }
    },
    refreshedListener = {
        when (it) {
            RefreshState.Loading -> {
                Log.d("hlc", "SimpleHeader Loading")
            }

            RefreshState.Success -> {
                Log.d("hlc", "SimpleHeader Success")
                refresh()
            }

            RefreshState.Error -> {
                Log.d("hlc", "SimpleHeader Error")
            }

            else -> Unit
        }
    },
    appendedListener = {
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
    },
    // 增量更新条件
    diffCallback = object : DiffUtil.ItemCallback<SingleTypeModel>() {
        override fun areItemsTheSame(oldItem: SingleTypeModel, newItem: SingleTypeModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SingleTypeModel, newItem: SingleTypeModel): Boolean {
            return oldItem.name == newItem.name
        }
    }
)