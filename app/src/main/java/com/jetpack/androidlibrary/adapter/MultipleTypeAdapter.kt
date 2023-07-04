package com.jetpack.androidlibrary.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.common.convertViewBinding
import com.example.common.viewHolder
import com.example.page.MultiplePagingDataAdapter
import com.example.page.RefreshState
import com.jetpack.androidlibrary.R
import com.jetpack.androidlibrary.data.MultipleTypeModel
import com.jetpack.androidlibrary.databinding.ItemTagBinding
import com.jetpack.androidlibrary.databinding.ItemUserBinding

/**
 * 多类型 type RecyclerView Adapter 实现。
 */
class MultipleTypeAdapter(
    // 创建单个手势事件回调、细分每个子项
    private val listener: (String) -> Unit
) : MultiplePagingDataAdapter<MultipleTypeModel>(
    // 为每个子项设置类型 type 值
    viewType = { type },
    // 创建 viewHolder
    create = { viewType ->
        when (viewType) {
            0 -> viewHolder<ItemUserBinding>()
            else -> viewHolder<ItemTagBinding>()
        }
    },
    // 对 itemView 进行数据绑定
    convert = { data, position ->
        when (data.type) {
            0 -> {
                val binding = convertViewBinding<ItemUserBinding>()
                binding.ivUserAvatar.setImageResource(R.drawable.ic_launcher_background)
                binding.tvUserName.text = data.user?.name
                binding.tvUserDescribe.text = data.user?.describe
            }

            else -> {
                val binding = convertViewBinding<ItemTagBinding>()
                binding.tvTag.text = "这个Tag是".plus(data.tag?.name)
            }
        }
    },
    // 点击事件区域
    clickScope = { type, adapter ->
        when (type) {
            0 -> {
                val binding = convertViewBinding<ItemUserBinding>()
                binding.root.setOnClickListener {
                    val itemModel = adapter.getNotNullItem(absoluteAdapterPosition - 1)
                    listener(itemModel.user?.name.toString())
                }
            }

            else -> {
                convertViewBinding<ItemTagBinding>()
                // Do Nothing There.
            }
        }
    },
    hasHeader = true,
    refreshedListener = {
        when (it) {
            RefreshState.Error -> {
                Log.d("hlc", "发生了错误")
            }

            else -> Unit
        }
    },
    // 增量更新条件
    diffCallback = object : DiffUtil.ItemCallback<MultipleTypeModel>() {
        override fun areItemsTheSame(oldItem: MultipleTypeModel, newItem: MultipleTypeModel): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: MultipleTypeModel, newItem: MultipleTypeModel): Boolean {
            return oldItem.type == newItem.type
        }
    }
)