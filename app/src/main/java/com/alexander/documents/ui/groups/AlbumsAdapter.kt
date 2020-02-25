package com.alexander.documents.ui.groups

import android.view.*
import android.view.animation.*
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
    private val albumLongClickListener: (position: Int, Album) -> Boolean
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
                albumLongClickListener(holder.adapterPosition, album)
            }

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

    private fun animateView(viewForAnimation: View) {
        val rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )

        val an = AnimationSet(true)
        an.isFillEnabled = true
        an.interpolator = BounceInterpolator()

        val ta = TranslateAnimation(-300f, 100f, 0f, 0f)
        ta.duration = 2000
        an.addAnimation(ta)

        val ta2 = TranslateAnimation(100f, 0f, 0f, 0f)
        ta2.duration = 2000
        ta2.startOffset = 2000 // allowing 2000 milliseconds for ta to finish
        an.addAnimation(ta2)

        an.addAnimation(rotate)

        rotate.duration = 4000
        rotate.repeatCount = Animation.INFINITE
        viewForAnimation.animation = an
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