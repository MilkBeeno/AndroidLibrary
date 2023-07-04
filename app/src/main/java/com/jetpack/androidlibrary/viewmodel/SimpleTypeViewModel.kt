package com.jetpack.androidlibrary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.page.PageResponse
import com.example.page.PageSource
import com.example.page.PageWrapper
import com.jetpack.androidlibrary.data.MultipleTypeModel
import com.jetpack.androidlibrary.data.SimpleTypeModel
import com.jetpack.androidlibrary.data.Tag
import com.jetpack.androidlibrary.data.UserModel
import kotlinx.coroutines.delay

class SimpleTypeViewModel : ViewModel() {
    val pageWrapper = PageWrapper(
        pageSize = 15,
        prefetchDistance = 1,
        pagingSourceFactory = {
            PageSource { requestNetwork(it) }
        }
    )

    private suspend fun requestNetwork(index: Int): PageResponse<SimpleTypeModel> {
        Log.d("hlc", "当前Index=$index")
        delay(1000)
        val data = mutableListOf<SimpleTypeModel>()
        for (i in 0..15) {
            data.add(SimpleTypeModel("我的名字是第 $index 组第 $i 号", "", "我的技能是拳打超人$index 组第${i} 拳"))
        }
        return PageResponse(code = 100, message = "", data = if (index == 10) null else data)
    }
}