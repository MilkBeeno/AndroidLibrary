package com.example.common

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// Activity 中获取视图 ViewBinding 设置视图并进行绑定
inline fun <reified V : ViewBinding> Activity.viewBinding() = lazy { inflateBinding<V>(layoutInflater).apply { setContentView(root) } }

// Dialog 中获取视图 ViewBinding 设置视图并进行绑定
inline fun <reified V : ViewBinding> Dialog.viewBinding() = lazy { inflateBinding<V>(layoutInflater).apply { setContentView(root) } }

// 反射获取 ViewBinging 实例
inline fun <reified V : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
    V::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as V

// Fragment 中获取视图 ViewBinding 设置视图并进行绑定
inline fun <reified V : ViewBinding> Fragment.viewBinding() = FragmentBindingDelegate(V::class.java)

// Activity 中获取视图 ViewBinding 设置视图并进行绑定
class FragmentBindingDelegate<V : ViewBinding>(private val clazz: Class<V>) : ReadOnlyProperty<Fragment, V> {
    private var binding: V? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        if (binding == null) {
            binding = clazz.getMethod("bind", View::class.java).invoke(null, thisRef.requireView()) as V
            // 当页面销毁是将 binding 视图内存释放掉、避免内存泄漏
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    binding = null
                }
            })
        }
        return checkNotNull(binding)
    }
}

// 通过反射获取 ViewHolderBinding 然后创建 ViewBindingViewHolder
inline fun <reified V : ViewBinding> ViewGroup.viewHolder(): ViewBindingViewHolder<V> {
    val method = V::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    val binding = method.invoke(null, LayoutInflater.from(context), this, false) as V
    return ViewBindingViewHolder(binding)
}

// 使用 ViewBinding 方式的 ViewHolder
class ViewBindingViewHolder<V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)

// 对 ViewHolder 进行强转成 ViewBindingViewHolder 类型
inline fun <reified V : ViewBinding> RecyclerView.ViewHolder.convertViewBinding(): V {
    return (this as ViewBindingViewHolder<V>).binding
}