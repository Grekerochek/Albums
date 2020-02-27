package com.alexander.documents.ui.groups

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.album_item.view.*
import com.alexander.documents.R
import com.alexander.documents.entity.Album
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/**
 * author alex
 */
class AlbumsAdapter(
    private val albumClickListener: (Album) -> Unit,
    private val albumLongClickListener: () -> Boolean,
    private val removeClickListener: (albumId: Int) -> Unit
) : ListAdapter<Album, RecyclerView.ViewHolder>(DiffCallback()) {

    var albums: MutableList<Album> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_item, parent, false)
        return AlbumsViewHolder(view)
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val album = albums[position]
        with(holder.itemView) {
            setOnClickListener { albumClickListener(album) }
            setOnLongClickListener {
                albumLongClickListener()
            }

            albumViewSelected.setOnClickListener { removeClickListener(album.id) }
            albumViewSelected.visibility = View.GONE
            albumTitleView.text = album.title
            albumCountView.text = context.getString(R.string.count_text, album.size.toString())

            Glide.with(albumImageView.context)
                .load(album.photoUrl)
                .apply(
                    RequestOptions().transforms(
                        CenterCrop(),
                        RoundedCorners(8)
                    )
                )
                .into(albumImageView)
        }
    }

    fun animateState() {
        albums.withIndex().forEach { (index, album) ->
            if (album.id > 0) {
                notifyItemChanged(index, ACTION_ANIMATE_ALBUMS)
            } else {
                notifyItemChanged(index, ACTION_SET_ALPHA_TO_ALBUM)
            }
        }
    }

    fun resetState() {
        notifyDataSetChanged()
    }

    companion object {
        const val ACTION_ANIMATE_ALBUMS: String = "action_animate_albums"
        const val ACTION_SET_ALPHA_TO_ALBUM: String = "action_alpha_albums"
    }

    class DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }

    class AlbumsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer
}