package com.jetpack.androidlibrary.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.common.ViewBindingViewHolder
import com.example.common.convertViewBinding
import com.example.common.viewHolder
import com.example.page.MultiplePagingDataAdapter
import com.jetpack.androidlibrary.R
import com.jetpack.androidlibrary.data.MultipleTypeModel
import com.jetpack.androidlibrary.databinding.ItemTagBinding
import com.jetpack.androidlibrary.databinding.ItemUserBinding

/**
 * 多类型 type RecyclerView Adapter 实现。
 */
class MultipleTypeAdapter(
    // 创建单个手势事件回调、细分每个子项
    private val listener: () -> Unit
) : MultiplePagingDataAdapter<MultipleTypeModel>(
    // 为每个子项设置类型 type 值
    viewType = { type },
    // 创建 viewHolder
    create = {
        when (it) {
            0 -> viewHolder<ItemUserBinding>()
            else -> viewHolder<ItemTagBinding>()
        }
    },
    // 对 itemView 进行数据绑定
    convert = { data, holder, position ->
        when (data.type) {
            0 -> {
                val binding = holder.convertViewBinding<ItemUserBinding>()
                binding.ivUserAvatar.setImageResource(R.drawable.ic_launcher_background)
                binding.tvUserName.text = data.user?.name
                binding.tvUserDescribe.text = data.user?.describe
            }

            else -> {
                val binding = holder.convertViewBinding<ItemTagBinding>()
                binding.tvTag.text = "这个Tag是".plus(data.tag?.name)
            }
        }
    },
    // 点击事件区域
    gestureScope = { type, holder ->
        when (type) {
            0 -> {
                val binding = holder.convertViewBinding<ItemUserBinding>()
                binding.root.setOnClickListener {
                    val itemModel = getNotNullItem(holder.absoluteAdapterPosition - 1)
                    Log.d("hlc", "当前的User是${itemModel.user?.name}")
                    listener()
                }
            }

            else -> {
                holder.convertViewBinding<ItemTagBinding>()
                // Do Nothing There.
            }
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