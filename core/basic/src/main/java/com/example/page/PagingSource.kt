package com.example.page

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Paging3 网络请求数据请求状态判断、自动处理分页的逻辑，强制要求 Integer 作为下拉刷新的标志。
 *
 * @param pageSize 分页大小
 * @param prefetchDistance 预加载长度
 * @param netWorkRequest 网络请求数据状态、以固定形式的 PagingResponse<T>，自动进行分页处理的逻辑回调在 load() 函数中
 */
fun <Value : Any> pager(
    pageSize: Int = 15,
    prefetchDistance: Int = 1,
    enablePlaceholders: Boolean = false,
    netWorkRequest: suspend (Int) -> PagingResponse<Value>
): Pager<Int, Value> {
    return Pager(
        config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = enablePlaceholders
        ),
        pagingSourceFactory = {
            object : PagingSource<Int, Value>() {
                // 实现必须定义如何从已加载分页数据的中间恢复刷新，使用 state.anchorPosition 作为最近访问的索引来映射正确的初始键。
                override fun getRefreshKey(state: PagingState<Int, Value>): Int? = null

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
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
        }
    )
}

/**
 * 用于 Paging3 网路请求数据、自动加载下一页数据解析模型、需严格按照 { code：“ ”，message：“ ”，data：“ ” } 的 Json 格式来、
 * 不然需要自己手动写网络请求数据状态和自动加载更多数据状态。
 */
data class PagingResponse<T>(val code: Int = 0, val message: String = "", val data: MutableList<T>? = null)
