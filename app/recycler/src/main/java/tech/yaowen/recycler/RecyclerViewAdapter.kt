package tech.yaowen.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(val itemWidth: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 20
    }

    inner class ItemViewHolder : RecyclerView.ViewHolder {
        constructor(parent: ViewGroup): super(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_item,
                parent,
                false
            )
        ) {
            val layoutParams = itemView.layoutParams
            layoutParams.width = itemWidth
        }

        init {
            itemView
        }
    }
}
