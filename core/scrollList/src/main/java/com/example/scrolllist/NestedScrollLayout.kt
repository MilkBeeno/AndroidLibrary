package com.example.scrolllist

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

class NestedScrollLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    NestedScrollView(context, attrs, defStyleAttr) {

    private var recyclerView: RecyclerView? = null

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(target, dx, dy, consumed, type)
        val scrollTopView = dy > 0 && scrollY < (recyclerView?.top ?: 0)
        if (scrollTopView) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        findRecyclerView(this)
    }

    private fun findRecyclerView(rootView: View) {
        when (rootView) {
            is RecyclerView -> {
                recyclerView = rootView
                return
            }

            is ViewGroup -> {
                for (i in 0 until rootView.childCount) {
                    findRecyclerView(rootView.getChildAt(i))
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        recyclerView?.layoutParams?.apply {
            // 将当前 NestedScrollView 的高度全部给 RecyclerView
            height = measuredHeight
            recyclerView?.layoutParams = this
        }
    }
}