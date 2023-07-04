package com.jetpack.androidlibrary.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.common.viewHolder
import com.example.page.RefreshState
import com.example.page.SimplePagingDataAdapter
import com.jetpack.androidlibrary.R
import com.jetpack.androidlibrary.data.SimpleTypeModel
import com.jetpack.androidlibrary.databinding.ItemUserBinding

/**
 * 单个列表适配器模版
 *
 * @property listener 表示子项中单个手势事件回调结果、按单个创建不同回调给 Activity
 */
class SimpleTypeAdapter(
    // 创建单个手势事件回调、细分每个子项
    private val listener: (String) -> Unit,
    private var refresh: () -> Unit
) : SimplePagingDataAdapter<SimpleTypeModel, ItemUserBinding>(
    // 创建 viewHolder
    create = { viewHolder() },
    // 对 itemView 进行数据绑定
    convert = { data, position ->
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
    hasHeader = true,
    refreshedListener = {
        when (it) {
            RefreshState.Error -> {
                Log.d("hlc", "发生了错误")
            }
            RefreshState.Success->{
                refresh()
            }

            else -> Unit
        }
    },
    // 增量更新条件
    diffCallback = object : DiffUtil.ItemCallback<SimpleTypeModel>() {
        override fun areItemsTheSame(oldItem: SimpleTypeModel, newItem: SimpleTypeModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SimpleTypeModel, newItem: SimpleTypeModel): Boolean {
            return oldItem.name == newItem.name
        }
    }
)