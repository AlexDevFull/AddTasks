package com.example.addtasks

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeToDeleteCallback(context: Context, private val listener: OnSwipeListener) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    interface OnSwipeListener {
        fun onItemSwiped(position: Int)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    // запускается первым при свайпе
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Получаем позицию элемента
        val position = viewHolder.adapterPosition
        listener.onItemSwiped(position)
    }
}
