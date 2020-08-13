package tech.yaowen.customview.ui.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.RecyclerItem1Binding

class ListAdapter<Object> : RecyclerView.Adapter<ListAdapter.Type1Holder>() {
    val items: MutableList<Object> = ArrayList<Object>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Type1Holder {
        val infalter = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<RecyclerItem1Binding>(infalter, R.layout.recycler_item1, parent, false)
        return Type1Holder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Type1Holder, position: Int) {
        val item : Object = items[position]
        holder.binding.setVariable(BR.item, item)
        holder.binding.executePendingBindings()
    }

    class Type1Holder(public val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {


    }

}