package com.raywenderlich.marsrovers.recyclerview


import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected var viewMap: MutableMap<Int, View> = HashMap()

    init {
        findViewItems(itemView)
    }

    fun setText(@IdRes id: Int, text: String) {
        val view = (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? TextView ?: throw IllegalArgumentException("View for $id is not a TextView")
        view.text = text
    }

    fun setText(@IdRes id: Int, @StringRes textRes: Int) {
        val view = (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? TextView ?: throw IllegalArgumentException("View for $id is not a TextView")
        view.setText(textRes)
    }


    fun setImage(@IdRes id: Int, @DrawableRes drawable: Int) {
        val view = viewMap[id]
        when (viewMap[id]) {
            is ImageView -> (view as ImageView).setImageResource(drawable)
            is TextView -> view?.setBackgroundResource(drawable)
            null -> throw IllegalArgumentException("View for $id not found")
            else ->
                throw IllegalArgumentException("View for $id is not a ImageView")

        }
    }

    fun setImage(@IdRes id: Int, drawable: Drawable) {
        val view = viewMap[id]
        when (viewMap[id]) {
            is ImageView -> (view as ImageView).setImageDrawable(drawable)
            is TextView -> view?.background = drawable
            null -> throw IllegalArgumentException("View for $id not found")
            else ->
                throw IllegalArgumentException("View for $id is not a ImageView")

        }
    }


    fun getImage(@IdRes id: Int): ImageView {
        val view = (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? ImageView ?: throw IllegalArgumentException("View for $id is not a ImageView")
        return view
    }

    protected fun findViewItems(itemView: View) {
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
