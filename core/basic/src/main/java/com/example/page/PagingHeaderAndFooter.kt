package com.example.page

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.viewbinding.ViewBinding
import com.example.common.ViewBindingViewHolder

/**
 * 使用 ViewBinding 封装 Paging3 的 PagingHeader 头部。
 *
 * @param create 使用 viewBindingViewHolder() 来创建。
 *  @param gestureScope 表示头部点击事件相关的逻辑在这处理。
 * @param convert 表示绑定 ViewHolder 后给创建者的一个回调。
 */
class PageHeaderAdapter<V : ViewBinding>(
    private val create: ViewGroup.() -> ViewBindingViewHolder<V>,
    private var gestureScope: (V.() -> Unit)? = null,
    private val convert: ((ViewBindingViewHolder<V>) -> Unit)? = null
) : LoadStateAdapter<ViewBindingViewHolder<V>>() {
    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return !(loadState is LoadState.NotLoading && !loadState.endOfPaginationReached)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewBindingViewHolder<V> {
        val viewHolder = create(parent)
        gestureScope?.invoke(viewHolder.binding)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewBindingViewHolder<V>, loadState: LoadState) {
        convert?.invoke(holder)
    }
}

/**
 * 使用 ViewBinding 封装 Paging3 的 PagingFooter 尾部。
 *
 * @param create 使用 viewBindingViewHolder() 来创建。
 * @param pageSize 表示每一页的大小。
 * @param hasHeader 表示是否有头部、用于计算是否还有更多数据。
 * @param gestureScope 表示尾部点击事件相关的逻辑在这处理。
 * @param convert 表示绑定 ViewHolder 后给创建者的一个回调。
 */
class PageFooterAdapter<V : ViewBinding>(
    private val create: ViewGroup.() -> ViewBindingViewHolder<V>,
    private var pageSize: Int = 0,
    private var hasHeader: Boolean = false,
    private var gestureScope: (V.() -> Unit)? = null,
    private val convert: ((LoadMoreState, ViewBindingViewHolder<V>) -> Unit)? = null,
) : LoadStateAdapter<ViewBindingViewHolder<V>>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewBindingViewHolder<V> {
        val viewHolder = create(parent)
        gestureScope?.invoke(viewHolder.binding)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewBindingViewHolder<V>, loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                convert?.invoke(LoadMoreState.Loading, holder)
            }

            is LoadState.Error -> {
                convert?.invoke(LoadMoreState.Error, holder)
            }

            is LoadState.NotLoading -> {
                val maxPageSize = pageSize + if (hasHeader) 1 else 0
                if (loadState.endOfPaginationReached && holder.absoluteAdapterPosition < maxPageSize) {
                    convert?.invoke(LoadMoreState.NoData, holder)
                }
            }
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return !(loadState is LoadState.NotLoading && !loadState.endOfPaginationReached)
    }

    enum class LoadMoreState { Loading, NoData, Error }
}