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
 * 4⃣️ 代码示例：
 *
 *        class SimpleTypeAdapter(
 *             // 创建单个手势事件回调、细分每个子项
 *             private val listener: (Int) -> Unit
 *         ) : SimplePagingDataAdapter<SimpleTypeModel, ItemUserBinding>(
 *
 *             // 创建 viewHolder
 *             create = { viewHolder() },
 *
 *             // 对 itemView 进行数据绑定
 *             convert = { data, binding, position ->
 *                 binding.ivUserAvatar.setImageResource(R.drawable.ic_launcher_background)
 *                 binding.tvUserName.text = data.name
 *                 binding.tvUserDescribe.text = data.describe
 *             },
 *
 *             // 点击事件区域
 *             gestureScope = { viewHolder ->
 *                 val binding = viewHolder.binding
 *                 binding.root.setOnClickListener {
 *                     // 因为在 RecyclerView 中添加了头部 Adapter 所以 absoluteAdapterPosition 的位置 index 是不正确的应当减 1
 *                     val itemModel = getNotNullItem(viewHolder.absoluteAdapterPosition - 1)
 *                     Log.d("hlc", "当前的User是${itemModel.name}")
 *                     listener(viewHolder.absoluteAdapterPosition)
 *                 }
 *             },
 *
 *             // 增量更新条件
 *             diffCallback = object : DiffUtil.ItemCallback<SimpleTypeModel>() {
 *                 override fun areItemsTheSame(oldItem: SimpleTypeModel, newItem: SimpleTypeModel): Boolean {
 *                     return oldItem.name == newItem.name
 *                 }
 *
 *                 override fun areContentsTheSame(oldItem: SimpleTypeModel, newItem: SimpleTypeModel): Boolean {
 *                     return oldItem.name == newItem.name
 *                 }
 *             }
 *         )
 *
 * @param create 创建 ViewBinding 方式下的 ViewHolder。
 * @param convert 绑定 ViewHolder 数据展示，相当于 onBindViewHolder()
 * @param gestureScope 手势事件监听区域、处理手势的时候需要特别注意的是，如果添加了 HeaderAdapter，获取 position 需要减 1
 * @param refreshedListener 下拉刷新状态监听。
 * @param appendedListener 上拉加载更多状态监听。
 * @param diffCallback 增量更新的条件。
 */
open class SimplePagingDataAdapter<T : Any, V : ViewBinding>(
    private var create: (ViewGroup.() -> ViewBindingViewHolder<V>),
    private var convert: ((T, V, Int) -> Unit)? = null,
    private var gestureScope: (SimplePagingDataAdapter<T, V>.(ViewBindingViewHolder<V>) -> Unit)? = null,
    private var refreshedListener: ((RefreshState) -> Unit)? = null,
    private var appendedListener: ((AppendState) -> Unit)? = null,
    private var diffCallback: DiffUtil.ItemCallback<T> = diffUtil()
) : PagingDataAdapter<T, ViewBindingViewHolder<V>>(diffCallback) {

    private var isRefreshing = false
    private var isAppending = false

    init {
        // 数据加载状态监听
        addLoadStateListener { combinedLoadStates ->
            when (combinedLoadStates.source.refresh) {
                is LoadState.Loading -> {
                    isRefreshing = true
                }

                is LoadState.NotLoading -> {
                    if (isRefreshing) {
                        isRefreshing = false
                        if (itemCount <= 0) {
                            refreshedListener?.invoke(RefreshState.Empty)
                        } else {
                            refreshedListener?.invoke(RefreshState.Success)
                        }
                    }
                }

                else -> {
                    refreshedListener?.invoke(
                        if (itemCount < 0) {
                            RefreshState.Error
                        } else {
                            RefreshState.Failed
                        }
                    )
                }
            }
            when (combinedLoadStates.source.append) {
                is LoadState.Loading -> {
                    isAppending = true
                }

                is LoadState.NotLoading -> {
                    if (isAppending) {
                        isAppending = false
                        appendedListener?.invoke(AppendState.Success)
                    }
                }

                else -> {
                    appendedListener?.invoke(AppendState.Failed)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewBindingViewHolder<V>, position: Int) {
        convert?.invoke(getNotNullItem(position), holder.binding, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<V> {
        return create(parent).apply { gestureScope?.invoke(this@SimplePagingDataAdapter, this) }
    }

    /** 获取指定单项 Item 的数据、若为空则抛出异常 */
    fun getNotNullItem(@IntRange(from = 0) position: Int): T {
        return checkNotNull(getItem(position))
    }
}

/**
 * 多类型 Paging3 分页 PagingDataAdapter 的封装，用法如下：
 *
 * 1⃣️ 代码示例：
 *
 *           class MultipleTypeAdapter(
 *               // 创建单个手势事件回调、细分每个子项
 *               private val listener: () -> Unit
 *           ) : MultiplePagingDataAdapter<MultipleTypeModel>(
 *               // 为每个子项设置类型 type 值
 *               viewType = { type },
 *               // 创建 viewHolder
 *               create = {
 *                   when (it) {
 *                       0 -> viewHolder<ItemUserBinding>()
 *                       else -> viewHolder<ItemTagBinding>()
 *                   }
 *               },
 *               // 对 itemView 进行数据绑定
 *               convert = { data, holder, position ->
 *                   when (data.type) {
 *                       0 -> {
 *                           val binding = holder.convertViewBinding<ItemUserBinding>()
 *                           binding.ivUserAvatar.setImageResource(R.drawable.ic_launcher_background)
 *                           binding.tvUserName.text = data.user?.name
 *                           binding.tvUserDescribe.text = data.user?.describe
 *                       }
 *
 *                       else -> {
 *                           val binding = holder.convertViewBinding<ItemTagBinding>()
 *                           binding.tvTag.text = "这个Tag是".plus(data.tag?.name)
 *                       }
 *                   }
 *               },
 *               // 点击事件区域
 *               gestureScope = { type, holder ->
 *                   when (type) {
 *                       0 -> {
 *                           val binding = holder.convertViewBinding<ItemUserBinding>()
 *                           binding.root.setOnClickListener {
 *                               val itemModel = getNotNullItem(holder.absoluteAdapterPosition)
 *                               Log.d("hlc", "当前的User是${itemModel.user?.name}")
 *                               listener()
 *                           }
 *                       }
 *
 *                       else -> {
 *                           holder.convertViewBinding<ItemTagBinding>()
 *                           // Do Nothing There.
 *                       }
 *                   }
 *               },
 *               // 增量更新条件
 *               diffCallback = object : DiffUtil.ItemCallback<MultipleTypeModel>() {
 *                   override fun areItemsTheSame(oldItem: MultipleTypeModel, newItem: MultipleTypeModel): Boolean {
 *                       return oldItem.type == newItem.type
 *                   }
 *
 *                   override fun areContentsTheSame(oldItem: MultipleTypeModel, newItem: MultipleTypeModel): Boolean {
 *                       return oldItem.type == newItem.type
 *                   }
 *               }
 *           )
 *
 * @param viewType 多类型分类。
 * @param create 创建 ViewBinding 方式下的 ViewHolder。
 * @param convert 绑定 ViewHolder 数据展示，相当于 onBindViewHolder()
 * @param gestureScope 手势事件监听区域、处理手势的时候需要特别注意的是，如果添加了 HeaderAdapter，获取 position 需要减 1
 * @param refreshedListener 下拉刷新状态监听。
 * @param appendedListener 上拉加载更多状态监听。
 * @param diffCallback 增量更新条件。
 */
open class MultiplePagingDataAdapter<T : Any>(
    private var viewType: (T.() -> Int),
    private var create: (ViewGroup.(Int) -> ViewHolder),
    private var convert: ((T, ViewHolder, Int) -> Unit)? = null,
    private var gestureScope: (MultiplePagingDataAdapter<T>.(Int, ViewHolder) -> Unit)? = null,
    private var refreshedListener: ((RefreshState) -> Unit)? = null,
    private var appendedListener: ((AppendState) -> Unit)? = null,
    private var diffCallback: DiffUtil.ItemCallback<T> = diffUtil()
) : PagingDataAdapter<T, ViewHolder>(diffCallback) {

    private var isRefreshing = false
    private var isAppending = false

    init {
        // 数据加载状态监听
        addLoadStateListener { combinedLoadStates ->
            when (combinedLoadStates.source.refresh) {
                is LoadState.Loading -> {
                    isRefreshing = true
                }

                is LoadState.NotLoading -> {
                    if (isRefreshing) {
                        isRefreshing = false
                        if (itemCount <= 0) {
                            refreshedListener?.invoke(RefreshState.Empty)
                        } else {
                            refreshedListener?.invoke(RefreshState.Success)
                        }
                    }
                }

                else -> {
                    refreshedListener?.invoke(
                        if (itemCount < 0) {
                            RefreshState.Error
                        } else {
                            RefreshState.Failed
                        }
                    )
                }
            }
            when (combinedLoadStates.source.append) {
                is LoadState.Loading -> {
                    isAppending = true
                }

                is LoadState.NotLoading -> {
                    if (isAppending) {
                        isAppending = false
                        appendedListener?.invoke(AppendState.Success)
                    }
                }

                else -> {
                    appendedListener?.invoke(AppendState.Failed)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { convert?.invoke(it, holder, position) }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType(getNotNullItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return create(parent, viewType).apply { gestureScope?.invoke(this@MultiplePagingDataAdapter, viewType, this) }
    }

    /** 获取指定单项 Item 的数据、若为空则抛出异常 */
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

/**
 * 列表数据加载的一个状态、由本地定义的一个状态适配 Paging3 的 LoadState 下拉刷新的状态。
 *
 * @property Success 表示 网络请求成功并且数据不为空。
 * @property Empty   表示 网络请求成功但数据为空。
 * @property Error   表示 网络请求失败且列表为空、通常是第一次刷新。
 * @property Failed  表示 网络请求失败但列表不为空、通常是第一次网络获取了数据、或者数据库中保存有数据。
 */
sealed class RefreshState {
    object Success : RefreshState()
    object Empty : RefreshState()
    object Error : RefreshState()
    object Failed : RefreshState()
}

/**
 * 列表数据加载的一个状态、由本地定义的一个状态适配 Paging3 的 LoadState 上拉加载的状态。
 *
 * @property Success 表示成功。
 * @property Failed  表示失败。
 */
sealed class AppendState {
    object Success : AppendState()
    object Failed : AppendState()
}
