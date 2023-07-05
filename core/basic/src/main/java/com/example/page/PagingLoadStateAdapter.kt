package com.example.page

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.viewbinding.ViewBinding
import com.example.common.ViewBindingViewHolder

/**
 * 使用 ViewBinding 封装 Paging3 的 PagingHeader 头，在 Header 中有三个状态将被用到 Loading、Success、Error。
 *
 * @param create 使用 viewBindingViewHolder() 来创建
 * @param clickScope 表示头部点击事件相关的逻辑在这处理
 * @param convert 表示绑定 ViewHolder 后给创建者的一个回调
 */
class PagingHeaderAdapter<V : ViewBinding>(
    private val create: ViewGroup.() -> ViewBindingViewHolder<V>,
    private var clickScope: (ViewBindingViewHolder<V>.() -> Unit)? = null,
    private val convert: (ViewBindingViewHolder<V>.() -> Unit)? = null
) : LoadStateAdapter<ViewBindingViewHolder<V>>() {

    // 当为绑定头部时需要展示头部并刷新一次界面
    private var isNotBindViewHolder: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewBindingViewHolder<V> {
        val viewHolder = create(parent)
        clickScope?.invoke(viewHolder)
        return viewHolder
    }

    // 当 LoadStateAdapter 头部时 LoadState 的值始终为 LoadState.NotLoading
    override fun onBindViewHolder(holder: ViewBindingViewHolder<V>, loadState: LoadState) {
        isNotBindViewHolder = false
        convert?.invoke(holder)
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return isNotBindViewHolder
    }
}

/**
 * 使用 ViewBinding 封装 Paging3 的 PagingFooter 尾部，在 Footer 中有三个状态将会被用到 Loading、Success、NoMoreData。
 *
 * @param pageSize 表示每一页的大小
 * @param hasHeader 表示是否有头部、用于计算是否还有更多数据
 * @param create 使用 viewBindingViewHolder() 来创建
 * @param clickScope 表示尾部点击事件相关的逻辑在这处理
 * @param convert 表示绑定 ViewHolder 后给创建者的一个回调
 */
class PagingFooterAdapter<V : ViewBinding>(
    private var pageSize: Int,
    private var hasHeader: Boolean,
    private val create: ViewGroup.() -> ViewBindingViewHolder<V>,
    private var clickScope: (ViewBindingViewHolder<V>.() -> Unit)? = null,
    private val convert: (ViewBindingViewHolder<V>.(AppendState) -> Unit)? = null,
) : LoadStateAdapter<ViewBindingViewHolder<V>>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewBindingViewHolder<V> {
        val viewHolder = create(parent)
        clickScope?.invoke(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewBindingViewHolder<V>, loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                convert?.invoke(holder, AppendState.Loading)
            }

            is LoadState.Error -> {
                convert?.invoke(holder, AppendState.Error)
            }

            is LoadState.NotLoading -> {
                val maxPageSize = pageSize + if (hasHeader) 1 else 0
                if (loadState.endOfPaginationReached && holder.bindingAdapterPosition < maxPageSize) {
                    convert?.invoke(holder, AppendState.NoMoreData)
                }
            }
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return !(loadState is LoadState.NotLoading && !loadState.endOfPaginationReached)
    }
}

/**
 * 列表数据加载的一个状态、由本地定义的一个状态适配 Paging3 的 LoadState 下拉刷新的状态。
 *
 * @property Loading 表示 加载中
 * @property Success 表示 加载成功并且数据不为空
 * @property Empty   表示 加载成功但数据为空
 * @property Error   表示 加载失败且列表为空、通常是第一次刷新
 * @property Failed  表示 加载失败但列表不为空、通常是第一次网络获取了数据、或者数据库中保存有数据
 */
sealed class RefreshState {
    object Loading : RefreshState()
    object Success : RefreshState()
    object Empty : RefreshState()
    object Error : RefreshState()
    object Failed : RefreshState()
}

/**
 * 列表数据加载的一个状态、由本地定义的一个状态适配 Paging3 的 LoadState 上拉加载更多的状态。
 *
 * @property Loading     表示 加载中
 * @property Success     表示 加载成功
 * @property Error       表示 加载发生错误
 * @property NoMoreData  表示 没有更多数据
 */
sealed class AppendState {
    object Loading : AppendState()
    object Success : AppendState()
    object Error : AppendState()
    object NoMoreData : AppendState()
}