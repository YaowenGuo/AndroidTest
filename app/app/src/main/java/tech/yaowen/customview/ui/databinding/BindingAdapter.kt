package tech.yaowen.customview.ui.databinding

import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import tech.yaowen.customview.R


@BindingAdapter("android:onLayoutChange")
fun setOnLayoutChangeListener(
    view: View,
    oldValue: View.OnLayoutChangeListener?,
    newValue: View.OnLayoutChangeListener?
) {
    Log.e("BindingFragment", "adapter")
    if (oldValue != null) {
        Log.e("BindingFragment", "old id" + System.identityHashCode(oldValue))
        view.removeOnLayoutChangeListener(oldValue)
    }
    if (newValue != null) {
        Log.e("BindingFragment", "new id" + System.identityHashCode(newValue))
        view.addOnLayoutChangeListener(newValue)
    }
}


// Translation from provided interfaces in Java:
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
interface OnViewDetachedFromWindow {
    fun onViewDetachedFromWindow(v: View)
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
interface OnViewAttachedToWindow {
    fun onViewAttachedToWindow(v: View)
}


@BindingAdapter(
    "android:onViewDetachedFromWindow",
    "android:onViewAttachedToWindow",
    requireAll = false
)
fun setListener(view: View, detach: OnViewDetachedFromWindow?, attach: OnViewAttachedToWindow?) {
    val newListener: View.OnAttachStateChangeListener?
    newListener = if (detach == null && attach == null) {
        null
    } else {
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                attach?.onViewAttachedToWindow(v)
            }

            override fun onViewDetachedFromWindow(v: View) {
                detach?.onViewDetachedFromWindow(v)
            }
        }
    }

    val oldListener: View.OnAttachStateChangeListener? =
        ListenerUtil.trackListener(view, newListener, R.id.onAttachStateChangeListener)
    if (oldListener != null) {
        view.removeOnAttachStateChangeListener(oldListener)
    }
    if (newListener != null) {
        view.addOnAttachStateChangeListener(newListener)
    }
}
