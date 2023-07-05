package com.jetpack.androidlibrary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.page.PagingResponse
import com.example.page.pager
import com.jetpack.androidlibrary.data.SimpleTypeModel
import kotlinx.coroutines.delay

class SimpleTypeViewModel : ViewModel() {

    val pager = pager { requestNetwork(it) }

    private suspend fun requestNetwork(index: Int): PagingResponse<SimpleTypeModel> {
        Log.d("hlc", "当前Index=$index")
        delay(1000)
        val data = mutableListOf<SimpleTypeModel>()
        for (i in 0..15) {
            data.add(SimpleTypeModel("我的名字是第 $index 组第 $i 号", "", "我的技能是拳打超人$index 组第${i} 拳"))
        }
        return PagingResponse(code = 100, message = "", data = if (index == 10) null else data)
    }
}