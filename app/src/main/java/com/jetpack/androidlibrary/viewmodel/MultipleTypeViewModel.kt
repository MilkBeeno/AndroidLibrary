package com.jetpack.androidlibrary.viewmodel

import androidx.lifecycle.ViewModel
import com.example.page.PageResponse
import com.example.page.PageSource
import com.example.page.PageWrapper
import com.jetpack.androidlibrary.data.MultipleTypeModel
import com.jetpack.androidlibrary.data.Tag
import com.jetpack.androidlibrary.data.UserModel

class MultipleTypeViewModel : ViewModel() {
    val pageWrapper = PageWrapper(
        pageSize = 15,
        prefetchDistance = 1,
        pagingSourceFactory = {
            PageSource { requestNetwork(it) }
        }
    )

    private fun requestNetwork(index: Int): PageResponse<MultipleTypeModel> {
        val data = mutableListOf<MultipleTypeModel>()
        for (i in 0 until 15) {
            if (i % 15 == 0) {
                data.add(MultipleTypeModel(type = 1, tag = Tag("这是第 $index 组第${i / 15} 个Tag")))
            } else {
                data.add(MultipleTypeModel(type = 0, user = UserModel("我的名字是第 $index 组第 $i 号", "", "我的技能是拳打超人$index 组第${i} 拳")))
            }
        }
        return PageResponse(code = 100, message = "", data = if (index == 5) null else data)
    }
}