package com.raywenderlich.marsrovers.recyclerview


import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private var viewMap: MutableMap<Int, View> = HashMap()

  init {
    findViewItems(itemView)
  }

  fun setText(@IdRes id: Int, text: String) {
    val view = (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? TextView ?: throw IllegalArgumentException("View for $id is not a TextView")
    view.text = text
  }

  fun getImage(@IdRes id: Int): ImageView {
    return (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? ImageView ?: throw IllegalArgumentException("View for $id is not a ImageView")
  }

  private fun findViewItems(itemView: View) {
    if (itemView is ViewGroup) {
      if (itemView.getId() != View.NO_ID) {
        viewMap.put(itemView.getId(), itemView)
      } else {
        viewMap.put(View.generateViewId(), itemView)
      }
      val childCount = itemView.childCount
      for (i in 0 until childCount) {
        val childView = itemView.getChildAt(i)
        if (childView.id != View.NO_ID) {
          viewMap.put(childView.id, childView)
        } else {
          viewMap.put(View.generateViewId(), childView)
        }
        if (childView is ViewGroup) {
          findViewItems(childView)
        }
      }
    } else {
      viewMap.put(itemView.id, itemView)
    }
  }
}
