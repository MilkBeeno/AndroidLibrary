package com.example.page

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow

/**
 * 对 Page<K,V> 的一层包裹、只暴露基本的请求参数和回调。
 */
class PageWrapper<Key : Any, Value : Any>(
    private val pageSize: Int = 15,
    private val prefetchDistance: Int = 1,
    private val pagingSourceFactory: () -> PagingSource<Key, Value>
) {
    val dataFlow: Flow<PagingData<Value>>
        get() = Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
}

/**
 * Paging3 网络请求数据请求状态判断、自动处理分页的逻辑。
 *
 * @param netWorkRequest 网络请求数据状态、以固定形式的 PagingResponse<T>,
 * 自动进行分页处理的逻辑回调在 load() 函数中。
 */
class PageSource<T : Any>(private val netWorkRequest: suspend (Int) -> PageResponse<T>) : PagingSource<Int, T>() {
    // 实现必须定义如何从已加载分页数据的中间恢复刷新，使用 state.anchorPosition 作为最近访问的索引来映射正确的初始键。
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val pageIndex = params.key ?: 1
            val apiPagingResponse = netWorkRequest(pageIndex)
            val result = apiPagingResponse.data
            val prevKey = if (pageIndex > 1) pageIndex - 1 else null
            val nextKey = if (result != null && result.size > 0) pageIndex + 1 else null
            LoadResult.Page(data = result ?: mutableListOf(), prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

/**
 * 用于 Paging3 网路请求数据、自动加载下一页数据解析模型、
 * 需严格按照 { code：“ ”，message：“ ”，data：“ ” } 的 Json 格式来、
 * 不然需要自己手动写网络请求数据状态和自动加载更多数据状态。
 */
data class PageResponse<T>(val code: Int = 0, val message: String = "", val data: MutableList<T>? = null)
