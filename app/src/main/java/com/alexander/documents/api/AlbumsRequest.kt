package com.alexander.documents.api

import android.net.Uri
import com.alexander.documents.entity.Album
import com.alexander.documents.entity.Photo
import com.alexander.documents.entity.VKFileUploadInfo
import com.alexander.documents.entity.VKServerUploadInfo
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKHttpPostCall
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * author alex
 */
class AlbumsRequest : ApiCommand<List<Album>>() {
    override fun onExecute(manager: VKApiManager): List<Album> {
        val call = VKMethodCall.Builder()
            .method("photos.getAlbums")
            .args("need_system", 1)
            .args("need_covers", 1)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserAlbums())
    }
}

class AlbumRequestCreate(
    private val title: String
) : ApiCommand<Album>() {
    override fun onExecute(manager: VKApiManager): Album {
        val call = VKMethodCall.Builder()
            .method("photos.createAlbum")
            .args("title", title)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserAlbumCreate())
    }
}

class AlbumRequestDelete(
    private val albumId: Int
) : ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {
        val call = VKMethodCall.Builder()
            .method("photos.deleteAlbum")
            .args("album_id", albumId)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserAlbumOrPhotoDelete())
    }
}

class PhotosRequest(
    private val albumId: Int
) : ApiCommand<List<Photo>>() {
    override fun onExecute(manager: VKApiManager): List<Photo> {
        val call = VKMethodCall.Builder()
            .method("photos.get")
            .args("album_id", albumId)
            .args("count", 1000)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserPhotos())
    }
}

class SavePhotoRequest(
    private val albumId: Int,
    private val photo: Uri
) : ApiCommand<String>() {
    override fun onExecute(manager: VKApiManager): String {
        val uploadInfo = getServerUploadInfo(manager)
        return uploadPhoto(photo, uploadInfo, manager)
    }

    private fun getServerUploadInfo(manager: VKApiManager): VKServerUploadInfo {
        val uploadInfoCall = VKMethodCall.Builder()
            .method("photos.getUploadServer")
            .args("album_id", albumId)
            .version(manager.config.version)
            .build()
        return manager.execute(uploadInfoCall, ServerUploadInfoParser())
    }

    private fun uploadPhoto(
        uri: Uri,
        serverUploadInfo: VKServerUploadInfo,
        manager: VKApiManager
    ): String {
        val fileUploadCall = VKHttpPostCall.Builder()
            .url(serverUploadInfo.uploadUrl)
            .args("photo", uri)
            .timeout(TimeUnit.MINUTES.toMillis(5))
            .build()
        val fileUploadInfo = manager.execute(fileUploadCall, null, FileUploadInfoParser())

        val saveCall = VKMethodCall.Builder()
            .method("photos.save")
            .args("album_id", albumId)
            .args("server", fileUploadInfo.server)
            .args("photos_list", fileUploadInfo.photosList)
            .args("hash", fileUploadInfo.hash)
            .args("aid", fileUploadInfo.aid)
            .version(manager.config.version)
            .build()

        return manager.execute(saveCall, ResponseApiParserPhotosAdd())
    }
}

private class ResponseApiParserAlbums : VKApiResponseParser<List<Album>> {
    override fun parse(response: String): List<Album> {
        try {
            val albumsResponse = JSONObject(response).getJSONObject("response")
            val items = albumsResponse.getJSONArray("items")
            val albums = ArrayList<Album>(items.length())
            for (i in 0 until items.length()) {
                val album = Album.parse(items.getJSONObject(i))
                albums.add(album)
            }
            return albums
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

class PhotoRequestDelete(
    private val photoId: Int
) : ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {
        val call = VKMethodCall.Builder()
            .method("photos.delete")
            .args("photo_id", photoId)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserAlbumOrPhotoDelete())
    }
}

private class ResponseApiParserAlbumOrPhotoDelete : VKApiResponseParser<Int> {
    override fun parse(response: String): Int {
        try {
            return JSONObject(response).optInt("response")
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ResponseApiParserAlbumCreate : VKApiResponseParser<Album> {
    override fun parse(response: String): Album {
        try {
            return Album.parse(JSONObject(response).getJSONObject("response"))
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ResponseApiParserPhotos : VKApiResponseParser<List<Photo>> {
    override fun parse(response: String): List<Photo> {
        try {
            val items = JSONObject(response).getJSONObject("response")
                .getJSONArray("items")
            val photos = ArrayList<Photo>(items.length())
            for (i in 0 until items.length()) {
                val photo = Photo.parse(items.getJSONObject(i))
                photos.add(photo)
            }
            return photos
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ResponseApiParserPhotosAdd : VKApiResponseParser<String> {
    override fun parse(response: String): String {
        try {
            return JSONObject(response).getString("response")
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ServerUploadInfoParser : VKApiResponseParser<VKServerUploadInfo> {
    override fun parse(response: String): VKServerUploadInfo {
        try {
            val joResponse = JSONObject(response).getJSONObject("response")
            return VKServerUploadInfo(
                uploadUrl = joResponse.getString("upload_url")
            )
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class FileUploadInfoParser : VKApiResponseParser<VKFileUploadInfo> {
    override fun parse(response: String): VKFileUploadInfo {
        try {
            val joResponse = JSONObject(response)
            return VKFileUploadInfo(
                server = joResponse.getString("server"),
                photosList = joResponse.getString("photos_list"),
                hash = joResponse.getString("hash"),
                aid = joResponse.getString("aid")
            )
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}