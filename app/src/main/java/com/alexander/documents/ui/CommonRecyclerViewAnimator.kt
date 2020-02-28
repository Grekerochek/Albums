package com.alexander.documents.ui

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.animation.*
import com.alexander.documents.ui.albums.AlbumsAdapter
import com.alexander.documents.ui.photos.PhotosAdapter
import kotlinx.android.synthetic.main.album_item.view.*
import kotlinx.android.synthetic.main.photo_item.view.*

/**
 * author alex
 */
class CommonRecyclerViewAnimator : DefaultItemAnimator() {

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: List<Any>
    ): ItemHolderInfo {
        if (changeFlags == RecyclerView.ItemAnimator.FLAG_CHANGED) {
            for (payload in payloads) {
                if (payload is String) {
                    return CommonItemHolderInfo(payload)
                }
            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {

        if (preInfo is CommonItemHolderInfo) {
            if (newHolder is AlbumsAdapter.AlbumsViewHolder) {
                if (AlbumsAdapter.ACTION_ANIMATE_ALBUMS == preInfo.updateAction) {
                    newHolder.itemView.albumViewSelected.visibility = View.VISIBLE
                    animateView(newHolder.itemView)
                } else if (AlbumsAdapter.ACTION_SET_ALPHA_TO_ALBUM == preInfo.updateAction) {
                    newHolder.itemView.alpha = 0.5f
                }
            } else if (newHolder is PhotosAdapter.PhotosViewHolder &&
               PhotosAdapter.ACTION_ANIMATE_PHOTOS == preInfo.updateAction) {
                newHolder.itemView.photoViewSelected.visibility = View.VISIBLE
                animateView(newHolder.itemView)
            }
        }
        return false
    }

    private fun animateView(viewForAnimation: View) {
        val rotate = RotateAnimation(
            -5f, 5f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 100
        rotate.repeatCount = Animation.INFINITE
        rotate.repeatMode = Animation.REVERSE
        viewForAnimation.animation = rotate
    }

    class CommonItemHolderInfo(var updateAction: String) :
        ItemHolderInfo()
}
