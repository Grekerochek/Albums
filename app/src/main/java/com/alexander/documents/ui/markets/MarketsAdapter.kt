package com.alexander.documents.ui.markets

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import com.alexander.documents.R
import com.alexander.documents.entity.Market
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.market_item.view.*

/**
 * author alex
 */
class MarketsAdapter(
    val marketClickListener: (Market) -> Unit
) : ListAdapter<Market, RecyclerView.ViewHolder>(DiffCallback()) {

    var markets: MutableList<Market> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.market_item, parent, false)
        return MarketsViewHolder(view)
    }

    override fun getItemCount(): Int = markets.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val market = markets[position]
        with(holder.itemView) {
            setOnClickListener { marketClickListener(market) }

            marketTitleView.text = market.title
            marketPriceView.text = context.getString(
                R.string.market_currency,
                market.price?.amount,
                market.price?.currency
            )

            Glide.with(marketImageView.context)
                .load(market.photo)
                .apply(
                    RequestOptions().transforms(
                        CenterCrop(),
                        RoundedCorners(8)
                    )
                )
                .into(marketImageView)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Market>() {
        override fun areItemsTheSame(oldItem: Market, newItem: Market): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Market, newItem: Market): Boolean {
            return oldItem == newItem
        }
    }

    class MarketsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer
}