package com.alexander.documents.ui.marketdetails

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.alexander.documents.R
import com.alexander.documents.entity.Market
import com.bumptech.glide.Glide
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
        toolbarButtonBack.setOnClickListener { onBackPressed() }
        buttonsContainer.displayedChild = if (market.isFavorite) 1 else 0
        Glide.with(this)
            .load(market.photo)
            .into(photoImageView)
        marketTitleView.text = market.title
        marketDescriptionView.text = market.description
        marketPriceView.text = getString(R.string.market_currency, market.price?.amount, market.price?.currency)
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

