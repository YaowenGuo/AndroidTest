package tech.yaowen.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

class RecyclerViewAdapter(val itemWidth: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val rows = 2;
    val columns = 3

    val pageCount = rows * columns


    val list = MutableList(2) { it }
    val EMPTY = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            EMPTY -> EmptyViewHolder(parent)
            else -> ItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (toDataPosition(position) < list.size) {
            if (holder is ItemViewHolder) {
                holder.setData(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return ceil(list.size.toDouble() / (rows * columns)).toInt() * (rows * columns)
    }


    override fun getItemViewType(position: Int): Int {
        val dataPosition = toDataPosition(position)
        return if (dataPosition < list.size) {
            1
        } else {
            EMPTY
        }
    }

    inner class ItemViewHolder : RecyclerView.ViewHolder {
        fun setData(position: Int) {

            positionText.text = "${toDataPosition(position)}"
        }

        val positionText: TextView

        constructor(parent: ViewGroup) : super(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_item,
                parent,
                false
            )
        ) {
            val layoutParams = itemView.layoutParams
            layoutParams.width = itemWidth
            positionText = itemView.findViewById(R.id.position)
        }

        init {
            itemView
        }
    }

    fun toDataPosition(viewPosition: Int): Int {
//        val page = viewPosition / pageCount
//        val offset = viewPosition % pageCount
//
//        return page * pageCount + if (offset % rows != 0) {
//            offset / rows + columns
//        } else {
//            offset / rows
//        }
        return viewPosition

    }


    inner class EmptyViewHolder : RecyclerView.ViewHolder {
        constructor(parent: ViewGroup) : super(LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_empty_item,
            parent,
            false
        )) {
            val layoutParams = itemView.layoutParams
            if (layoutParams != null) {
                layoutParams.width = itemWidth
            }

        }
    }
}
