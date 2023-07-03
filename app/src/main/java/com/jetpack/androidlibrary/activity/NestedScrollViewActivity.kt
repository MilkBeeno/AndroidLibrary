package com.jetpack.androidlibrary.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.viewBinding
import com.jetpack.androidlibrary.R
import com.jetpack.androidlibrary.databinding.ActivityNestedScrollViewBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NestedScrollViewActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, NestedScrollViewActivity::class.java))
    }

    private val binding: ActivityNestedScrollViewBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val rvAdapter = RvAdapter()
        binding.recyclerView.adapter = rvAdapter
        MainScope().launch {
            delay(2000)
            rvAdapter.updateData(getData())
        }
    }

    private class RvAdapter(private var data: MutableList<String> = mutableListOf()) : RecyclerView.Adapter<RvAdapter.RvViewHolder>() {
        private var viewHolderCount: Int = 0

        inner class RvViewHolder(item: View) : RecyclerView.ViewHolder(item)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nested_scroll_view, parent, false)
            viewHolderCount++
            Log.d("hlc - nestedScrollView", "创建了 $viewHolderCount 个 ViewHolder")
            return RvViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
            holder.itemView.findViewById<AppCompatTextView>(R.id.tvNested).text = data[position]
        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateData(data: MutableList<String>) {
            this.data.addAll(data)
            notifyDataSetChanged()
        }
    }


    private fun getData(): MutableList<String> {
        return mutableListOf<String>().apply {
            for (i in 0 until 90) {
                add("当前的 Position 是$i")
            }
        }
    }
}