package com.alexander.documents.ui.markets

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.alexander.documents.R
import com.alexander.documents.api.MarketsRequest
import com.alexander.documents.entity.Group
import com.alexander.documents.entity.Market
import com.alexander.documents.ui.marketdetails.MarketDetailsActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import kotlinx.android.synthetic.main.activity_market_list.*
import kotlinx.android.synthetic.main.activity_market_list.containerView
import kotlinx.android.synthetic.main.activity_market_list.toolbarButton
import kotlinx.android.synthetic.main.activity_market_list.toolbarTitleView

class MarketListActivity : AppCompatActivity() {

    private val group: Group by lazy(LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(EXTRA_GROUP) as Group
    }

    private val marketsAdapter: MarketsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MarketsAdapter(::onMarketClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_list)
        initViews()
        requestMarkets()
    }

    private fun initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            containerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        toolbarTitleView.text = getString(R.string.market_title, group.name)
        toolbarButton.setOnClickListener { onBackPressed() }
        recyclerViewMarkets.layoutManager = GridLayoutManager(this, 2)
        recyclerViewMarkets.adapter = marketsAdapter
        containerView.setOnRefreshListener { requestMarkets() }
    }

    private fun requestMarkets() {
        containerView.isRefreshing = true
        VK.execute(MarketsRequest(group.id), object : VKApiCallback<List<Market>> {
            override fun success(result: List<Market>) {
                if (!isFinishing) {
                    containerView.isRefreshing = false
                    marketsAdapter.markets = result.toMutableList()
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

    private fun onMarketClick(market: Market) {
        startActivity(MarketDetailsActivity.createIntent(this, market))
    }

    companion object {
        private const val EXTRA_GROUP = "extra_group"

        fun createIntent(context: Context, group: Group): Intent {
            return Intent(context, MarketListActivity::class.java)
                .putExtra(EXTRA_GROUP, group)
        }
    }
}
