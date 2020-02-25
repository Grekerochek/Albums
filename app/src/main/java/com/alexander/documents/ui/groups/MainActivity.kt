package com.alexander.documents.ui.groups

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.view.animation.BounceInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import com.alexander.documents.R
import com.alexander.documents.api.AlbumsRequest
import com.alexander.documents.entity.Album

/**
 * author alex
 */
class MainActivity : AppCompatActivity() {

    private var stateIsEdit: Boolean = false

    private val albumsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        AlbumsAdapter(::onAlbumClick, ::onAlbumLongClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            containerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        if (!VK.isLoggedIn()) {
            VK.login(this, listOf(VKScope.PHOTOS))
        } else {
            init()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                init()
            }

            override fun onLoginFailed(errorCode: Int) {
                showAuthError()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun init() {
        setSupportActionBar(toolbar)
        recyclerViewAlbums.layoutManager = GridLayoutManager(this, 2)
        recyclerViewAlbums.adapter = albumsAdapter
        containerView.setOnRefreshListener {
            requestAlbums()
        }
        requestAlbums()
    }

    private fun requestAlbums() {
        containerView.isRefreshing = true
        VK.execute(AlbumsRequest(), object : VKApiCallback<List<Album>> {
            override fun success(result: List<Album>) {
                if (!isFinishing) {
                    containerView.isRefreshing = false
                    albumsAdapter.albums = result.toMutableList()
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

    private fun showAuthError() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_auth_message))
            .setPositiveButton(R.string.ok) { _, _ -> VK.login(this, arrayListOf(VKScope.PHOTOS)) }
            .setNegativeButton(R.string.cancel) { _, _ -> finish() }
            .show()
    }

    private fun onAlbumClick(album: Album) {
    }

    private fun onAlbumLongClick(position: Int, album: Album): Boolean {
        stateIsEdit = true
        toolbarTitleView.text = getString(R.string.edit_title)
        toolbarExitButton.visibility = View.VISIBLE
        toolbarExitButton.setOnClickListener { resetState() }
        toolbarAddButton.visibility = View.GONE
        toolbarEditButton.visibility = View.GONE
        return true
    }

    private fun resetState() {

    }

    companion object {

        fun createIntent(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}
