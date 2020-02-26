package com.alexander.documents.ui.groups

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.animation.*
import kotlinx.android.synthetic.main.album_item.view.*


class AlbumsAnimator : DefaultItemAnimator() {

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
                    return CharacterItemHolderInfo(payload)
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

        if (preInfo is CharacterItemHolderInfo) {
            val holder = newHolder as AlbumsAdapter.AlbumsViewHolder
            if (AlbumsAdapter.ACTION_ANIMATE_ALBUMS == preInfo.updateAction) {
                holder.itemView.albumViewSelected.visibility = View.VISIBLE
                animateView(holder.itemView)
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

    class CharacterItemHolderInfo(var updateAction: String) :
        ItemHolderInfo()
}
