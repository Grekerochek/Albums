package com.alexander.documents.ui.markets

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.alexander.documents.PathUtils
import com.alexander.documents.R
import com.alexander.documents.api.PhotosRequest
import com.alexander.documents.api.SavePhotoRequest
import com.alexander.documents.entity.Album
import com.alexander.documents.entity.Photo
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.android.synthetic.main.activity_album_details.*
import kotlinx.android.synthetic.main.activity_album_details.containerView
import kotlinx.android.synthetic.main.activity_album_details.toolbarButtonBack

class AlbumDetailsActivity : AppCompatActivity() {

    private val album: Album by lazy(LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(EXTRA_ALBUM) as Album
    }

    private val photosAdapter: PhotosAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PhotosAdapter(::onPhotoClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)
        initViews()
        requestPhotos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            addPhoto(imageUri = data.data!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (
            requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            selectPhoto()
        }
    }

    private fun initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            containerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        toolbarButtonAdd.setOnClickListener { askPermissionReadExternalStorage() }
        toolbarButtonBack.setOnClickListener { onBackPressed() }
        albumTitleView.text = album.title
        recyclerViewPhotos.layoutManager = GridLayoutManager(this, 3)
        recyclerViewPhotos.adapter = photosAdapter
        containerView.setOnRefreshListener { requestPhotos() }
    }

    private fun requestPhotos() {
        containerView.isRefreshing = true
        VK.execute(PhotosRequest(album.id), object : VKApiCallback<List<Photo>> {
            override fun success(result: List<Photo>) {
                if (!isFinishing) {
                    containerView.isRefreshing = false
                    photosAdapter.photos = result.toMutableList()
                }
            }

            override fun fail(error: Exception) {
                showError()
            }
        })
    }

    private fun askPermissionReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            selectPhoto()
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun addPhoto(imageUri: Uri) {
        val userId =
            VKAccessToken.restore(getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE))
                ?.userId
        if (userId == null) {
            showError()
            return
        }
        containerView.isRefreshing = true
        val photo = Uri.parse(PathUtils.getPath(this, imageUri))
        VK.execute(SavePhotoRequest(album.id, photo), object : VKApiCallback<String> {
            override fun success(result: String) {
                requestPhotos()
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

    private fun onPhotoClick(photo: Photo) {
    }

    companion object {
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
        private const val PREFERENCE_NAME = "com.vkontakte.android_pref_name"
        private const val GALLERY_REQUEST_CODE = 0
        private const val EXTRA_ALBUM = "extra_album"

        fun createIntent(context: Context, album: Album): Intent {
            return Intent(context, AlbumDetailsActivity::class.java)
                .putExtra(EXTRA_ALBUM, album)
        }
    }
}
