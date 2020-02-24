package com.alexander.documents.ui.marketdetails

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.alexander.documents.R
import com.alexander.documents.api.FaveRequestAdd
import com.alexander.documents.api.FaveRequestDelete
import com.alexander.documents.entity.Market
import com.bumptech.glide.Glide
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import kotlinx.android.synthetic.main.activity_market_details.*

class MarketDetailsActivity : AppCompatActivity() {

    private val market: Market by lazy(LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(EXTRA_MARKET) as Market
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_details)
        initViews()
    }

    private fun initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            containerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        containerView.setOnRefreshListener { containerView.isRefreshing = false }
        toolbarTitleView.text = getString(R.string.market_title, market.title)
        toolbarButton.setOnClickListener { onBackPressed() }
        buttonsContainer.displayedChild = if (market.isFavorite) 1 else 0
        addButton.setOnClickListener { addToTheFavorites() }
        deleteButton.setOnClickListener { deleteFormTheFavorites() }
        Glide.with(this)
            .load(market.photo)
            .into(marketImageView)
        marketTitleView.text = market.title
        marketDescriptionView.text = market.description
        marketPriceView.text = getString(R.string.market_currency, market.price?.amount, market.price?.currency)
    }

    private fun addToTheFavorites() {
        containerView.isRefreshing = true
        VK.execute(FaveRequestAdd(market.ownerId, market.id), object : VKApiCallback<Int> {
            override fun success(result: Int) {
                if (!isFinishing && result == 1) {
                    containerView.isRefreshing = false
                    buttonsContainer.displayedChild = 0
                }
            }

            override fun fail(error: Exception) {
                showError()
            }
        })
    }

    private fun deleteFormTheFavorites() {
        containerView.isRefreshing = true
        VK.execute(FaveRequestDelete(market.ownerId, market.id), object : VKApiCallback<Int> {
            override fun success(result: Int) {
                if (!isFinishing && result == 1) {
                    containerView.isRefreshing = false
                    buttonsContainer.displayedChild = 1
                }
            }

            override fun fail(error: Exception) {
                showError()
            }
        })
    }

    private fun showError() {
        containerView.isRefreshing = false
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_message))
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        private const val EXTRA_MARKET = "extra_market"

        fun createIntent(context: Context, market: Market): Intent {
            return Intent(context, MarketDetailsActivity::class.java)
                .putExtra(EXTRA_MARKET, market)
        }
    }
}

