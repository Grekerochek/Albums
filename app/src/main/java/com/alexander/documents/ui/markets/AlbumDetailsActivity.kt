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
import com.alexander.documents.entity.Album
import com.alexander.documents.entity.Group
import com.alexander.documents.entity.Market
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import kotlinx.android.synthetic.main.activity_market_list.*
import kotlinx.android.synthetic.main.activity_market_list.containerView
import kotlinx.android.synthetic.main.activity_market_list.toolbarButton
import kotlinx.android.synthetic.main.activity_market_list.toolbarTitleView

class AlbumDetailsActivity : AppCompatActivity() {

    private val album: Album by lazy(LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(EXTRA_ALBUM) as Album
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
       // toolbarTitleView.text = getString(R.string.market_title, group.name)
        toolbarButton.setOnClickListener { onBackPressed() }
        recyclerViewMarkets.layoutManager = GridLayoutManager(this, 2)
        recyclerViewMarkets.adapter = marketsAdapter
        containerView.setOnRefreshListener { requestMarkets() }
    }

    private fun requestMarkets() {
        containerView.isRefreshing = true
        VK.execute(MarketsRequest(album.id), object : VKApiCallback<List<Market>> {
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
    }

    companion object {
        private const val EXTRA_ALBUM = "extra_album"

        fun createIntent(context: Context, album: Album): Intent {
            return Intent(context, AlbumDetailsActivity::class.java)
                .putExtra(EXTRA_ALBUM, album)
        }
    }
}
