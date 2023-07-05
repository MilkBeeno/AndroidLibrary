package com.example.page

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.example.common.ViewBindingViewHolder

/**
 * 单类型 Paging3 分页 PagingDataAdapter 的封装，用法如下：
 *
 * 1⃣️ 没有对 Header 头部、Footer 尾部进行封装，由 PagingDataAdapter API 自行添加如下：
 *    withLoadStateHeaderAndFooter()、withLoadStateHeader() 和 withLoadStateFooter()。
 *
 * 2⃣️ 继承 SimplePagingDataAdapter 类型、然后实现 create() 回调创建 ViewHolder，
 *    覆写 onBindViewHolder()、onCreateViewHolder() 等函数。
 *
 * 3⃣️ 直接通过 new 方式创建 SimplePagingDataAdapter 对象并设置各监听回调。
 *
 * 4⃣️ 代码示例：@see <a href="https://github.com/MilkBeeno/AndroidLibrary/tree/main"</a>
 *
 * @param create 创建 ViewBinding 方式下的 ViewHolder
 * @param convert 绑定 ViewHolder 数据展示，相当于 onBindViewHolder()
 * @param clickScope 手势事件监听区域、处理手势的时候需要特别注意的是，如果添加了 HeaderAdapter，获取 position 需要减 1
 * @param hasHeader 是否有 header 此处一定要注意，若有需改变状态为 true 否则影响 itemCount 的计算导致状态设置错误
 * @param refreshedListener 下拉刷新状态监听
 * @param appendedListener 上拉加载更多状态监听
 * @param diffCallback 增量更新的条件
 */
open class SinglePagingDataAdapter<T : Any, V : ViewBinding>(
    private val hasHeader: Boolean,
    private var create: (ViewGroup.() -> ViewBindingViewHolder<V>),
    private var convert: (ViewBindingViewHolder<V>.(T, Int) -> Unit)? = null,
    private var clickScope: (ViewBindingViewHolder<V>.(SinglePagingDataAdapter<T, V>, Boolean) -> Unit)? = null,
    private var refreshedListener: ((RefreshState) -> Unit)? = null,
    private var appendedListener: ((AppendState) -> Unit)? = null,
    private var diffCallback: DiffUtil.ItemCallback<T> = diffUtil()
) : PagingDataAdapter<T, ViewBindingViewHolder<V>>(diffCallback) {

    private var isRefreshing = false
    private var isAppending = false

    init {
        // 数据加载状态监听
        addLoadStateListener { combinedLoadStates ->
            val maxCount = if (hasHeader) 1 else 0
            when (combinedLoadStates.source.refresh) {
                is LoadState.Loading -> {
                    isRefreshing = true
                    refreshedListener?.invoke(RefreshState.Loading)
                }

                is LoadState.NotLoading -> {
                    if (isRefreshing) {
                        isRefreshing = false
                        refreshedListener?.invoke(if (itemCount <= maxCount) RefreshState.Empty else RefreshState.Success)
                    }
                }

                is LoadState.Error -> {
                    isRefreshing = false
                    refreshedListener?.invoke(if (itemCount < maxCount) RefreshState.Error else RefreshState.Failed)
                }
            }
            when (combinedLoadStates.source.append) {
                is LoadState.Loading -> {
                    isAppending = true
                    appendedListener?.invoke(AppendState.Loading)
                }

                is LoadState.NotLoading -> {
                    if (isAppending) {
                        isAppending = false
                        appendedListener?.invoke(AppendState.Success)
                    }
                }

                is LoadState.Error -> {
                    isRefreshing = false
                    appendedListener?.invoke(AppendState.Error)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewBindingViewHolder<V>, position: Int) {
        convert?.invoke(holder, getNotNullItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<V> {
        return create(parent).apply {
            clickScope?.invoke(this, this@SinglePagingDataAdapter, hasHeader)
        }
    }

    /** 获取指定单项 Item 的数据、若为空则抛出异常 */
    fun getNotNullItem(@IntRange(from = 0) position: Int): T {
        return checkNotNull(getItem(position))
    }
}

/**
 * 多类型 Paging3 分页 PagingDataAdapter 的封装，用法如下：
 *
 * 1⃣️ 代码示例：@see <a href="https://github.com/MilkBeeno/AndroidLibrary/tree/main"</a>
 *
 * @param viewType 多类型分类
 * @param create 创建 ViewBinding 方式下的 ViewHolder
 * @param convert 绑定 ViewHolder 数据展示，相当于 onBindViewHolder()
 * @param clickScope 手势事件监听区域、处理手势的时候需要特别注意的是，如果添加了 HeaderAdapter，获取 position 需要减 1
 * @param hasHeader 是否有 header 此处一定要注意，若有需改变状态为 true 否则影响 itemCount 的计算导致状态设置错误
 * @param refreshedListener 下拉刷新状态监听
 * @param appendedListener 上拉加载更多状态监听
 * @param diffCallback 增量更新条件
 */
open class MultiplePagingDataAdapter<T : Any>(
    private var viewType: (T.() -> Int),
    private var create: (ViewGroup.(Int) -> ViewHolder),
    private var convert: (ViewHolder.(T, Int) -> Unit)? = null,
    private var clickScope: (ViewHolder.(Int, MultiplePagingDataAdapter<T>, Boolean) -> Unit)? = null,
    private val hasHeader: Boolean = false,
    private var refreshedListener: ((RefreshState) -> Unit)? = null,
    private var appendedListener: ((AppendState) -> Unit)? = null,
    private var diffCallback: DiffUtil.ItemCallback<T> = diffUtil()
) : PagingDataAdapter<T, ViewHolder>(diffCallback) {

    private var isRefreshing = false
    private var isAppending = false

    init {
        // 数据加载状态监听
        addLoadStateListener { combinedLoadStates ->
            val maxCount = if (hasHeader) 1 else 0
            when (combinedLoadStates.source.refresh) {
                is LoadState.Loading -> {
                    isRefreshing = true
                    refreshedListener?.invoke(RefreshState.Loading)
                }

                is LoadState.NotLoading -> {
                    if (isRefreshing) {
                        isRefreshing = false
                        refreshedListener?.invoke(if (itemCount <= maxCount) RefreshState.Empty else RefreshState.Success)
                    }
                }

                is LoadState.Error -> {
                    isRefreshing = false
                    refreshedListener?.invoke(if (itemCount < maxCount) RefreshState.Error else RefreshState.Failed)
                }
            }
            when (combinedLoadStates.source.append) {
                is LoadState.Loading -> {
                    isAppending = true
                    appendedListener?.invoke(AppendState.Loading)
                }

                is LoadState.NotLoading -> {
                    if (isAppending) {
                        isAppending = false
                        appendedListener?.invoke(AppendState.Success)
                    }
                }

                is LoadState.Error -> {
                    isAppending = false
                    appendedListener?.invoke(AppendState.Error)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { convert?.invoke(holder, it, position) }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType(getNotNullItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return create(parent, viewType).apply {
            clickScope?.invoke(this, viewType, this@MultiplePagingDataAdapter, hasHeader)
        }
    }

    /** 获取指定单项 Item 的数据、若为空则抛出异常。 */
    fun getNotNullItem(@IntRange(from = 0) position: Int): T {
        return checkNotNull(getItem(position))
    }
}

/** 默认不设置任何限制条件、只要通知到就更新数据。 */
private fun <T : Any> diffUtil(): DiffUtil.ItemCallback<T> {
    return object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return false
        }
    }
}

