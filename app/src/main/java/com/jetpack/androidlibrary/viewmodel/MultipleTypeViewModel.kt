package com.jetpack.androidlibrary.viewmodel

import androidx.lifecycle.ViewModel
import com.example.page.PagingResponse
import com.example.page.pager
import com.jetpack.androidlibrary.data.MultipleTypeModel
import com.jetpack.androidlibrary.data.Tag
import com.jetpack.androidlibrary.data.UserModel

class MultipleTypeViewModel : ViewModel() {

    val pager = pager { requestNetwork(it) }

    private fun requestNetwork(index: Int): PagingResponse<MultipleTypeModel> {
        val data = mutableListOf<MultipleTypeModel>()
        for (i in 0 until 15) {
            if (i % 15 == 0) {
                data.add(MultipleTypeModel(type = 1, tag = Tag("这是第 $index 组第${i / 15} 个Tag")))
            } else {
                data.add(MultipleTypeModel(type = 0, user = UserModel("我的名字是第 $index 组第 $i 号", "", "我的技能是拳打超人$index 组第${i} 拳")))
            }
        }
        return PagingResponse(code = 100, message = "", data = if (index == 5) null else data)
    }
}