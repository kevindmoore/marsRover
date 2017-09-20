package com.raywenderlich.marsrovers.recyclerview

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.raywenderlich.marsrovers.R
import com.raywenderlich.marsrovers.models.PhotoRow
import com.raywenderlich.marsrovers.models.ROW_TYPE


class PhotoAdapter(private var photoList: ArrayList<PhotoRow>) : RecyclerView.Adapter<DefaultViewHolder>() {
    private var filteredPhotos = ArrayList<PhotoRow>()
    private var filtering = false
    private var layoutInflator: LayoutInflater? = null

    override fun getItemCount(): Int {
        if (filtering) {
            return filteredPhotos.size
        }
        return photoList.size
    }

    fun updatePhotos(photos : ArrayList<PhotoRow>) {
        DiffUtil.calculateDiff(PhotoRowDiffCallback(photos, photoList)).dispatchUpdatesTo(this)
        photoList = photos
        clearFilter()
    }

    private fun clearFilter() {
        filtering = false
        filteredPhotos.clear()
    }

    fun removeRow(row : Int) {
        if (filtering) {
            filteredPhotos.removeAt(row)
        } else {
            photoList.removeAt(row)
        }
        notifyItemRemoved(row)
    }

    fun filterCamera(camera: String) {
        filtering = true
        filteredPhotos.clear()
        val newPhotos = photoList.filter { photo -> photo.type == ROW_TYPE.PHOTO && photo.photo?.camera?.name.equals(camera) } as ArrayList<PhotoRow>
        DiffUtil.calculateDiff(PhotoRowDiffCallback(newPhotos, photoList)).dispatchUpdatesTo(this)
        filteredPhotos = newPhotos
    }

    override fun getItemViewType(position: Int): Int {
        return if (filtering) {
            filteredPhotos[position].type.ordinal
        } else {
            photoList[position].type.ordinal
        }
    }

    override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {
        val photoRow : PhotoRow = if (filtering) {
            filteredPhotos[position]
        } else {
            photoList[position]
        }
        if (photoRow.type == ROW_TYPE.PHOTO) {
            val photo = photoRow.photo
            Glide.with(holder.itemView.context)
                    .load(photo?.img_src)
                    .into(holder.getImage(R.id.camera_image))
            holder.setText(R.id.date, photo?.earth_date!!)
        } else {
            holder.setText(R.id.header_text, photoRow.header!!)
        }
        setAnimation(holder.itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
        var inflatedView : View? = null
        if (layoutInflator == null) {
            layoutInflator = LayoutInflater.from(parent.context)
        }
        when (viewType) {
            ROW_TYPE.PHOTO.ordinal -> inflatedView = layoutInflator?.inflate(R.layout.row_item, parent,false)
            ROW_TYPE.HEADER.ordinal -> inflatedView = layoutInflator?.inflate(R.layout.header_item, parent,false)
        }
        return DefaultViewHolder(inflatedView!!)
    }

    private fun setAnimation(viewToAnimate: View) {
        if (viewToAnimate.animation == null) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.animation = animation
        }
    }

    class PhotoRowDiffCallback(val newRows : List<PhotoRow>, val oldRows : List<PhotoRow>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            return oldRow.type == newRow.type
        }

        override fun getOldListSize(): Int = oldRows.size

        override fun getNewListSize(): Int = newRows.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            return oldRow.equals(newRow)
        }
    }
}