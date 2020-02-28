package com.alexander.documents.ui.photos

import android.view.*
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import com.alexander.documents.R
import com.alexander.documents.entity.Photo
import kotlinx.android.synthetic.main.photo_item.view.*

/**
 * author alex
 */
class PhotosAdapter(
    private val photoClickListener: (Photo) -> Unit,
    private val photoLongClickListener: () -> Boolean,
    private val deletePhotoClickListener: (Photo) -> Unit
) : ListAdapter<Photo, RecyclerView.ViewHolder>(DiffCallback()) {

    var photos: MutableList<Photo> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)
        return PhotosViewHolder(view)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photo = photos[position]
        with(holder.itemView) {
            setOnClickListener { photoClickListener(photo) }
            setOnLongClickListener { photoLongClickListener() }
            photoViewSelected.setOnClickListener { deletePhotoClickListener(photo) }

            Glide.with(photoImageView.context)
                .load(photo.url)
                .apply(
                    RequestOptions().transforms(
                        CenterCrop()
                    )
                )
                .into(photoImageView)
        }
    }

    fun animateState() {
        notifyItemRangeChanged(0, photos.size, ACTION_ANIMATE_PHOTOS)
    }

    fun resetState() {
        notifyDataSetChanged()
    }

    companion object {
        const val ACTION_ANIMATE_PHOTOS: String = "action_animate_photos"
    }

    class DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    class PhotosViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer
}