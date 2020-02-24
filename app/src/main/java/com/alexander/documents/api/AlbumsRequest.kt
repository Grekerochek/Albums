package com.alexander.documents.api

import com.alexander.documents.entity.Album
import com.alexander.documents.entity.City
import com.alexander.documents.entity.Group
import com.alexander.documents.entity.Market
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

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

class CitiesRequest : ApiCommand<List<City>>() {
    override fun onExecute(manager: VKApiManager): List<City> {
        val call = VKMethodCall.Builder()
            .method("database.getCities")
            .args("country_id", 1)
            .args("count", 1000)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserCities())
    }
}

class MarketsRequest(private val ownerId: Int) : ApiCommand<List<Market>>() {
    override fun onExecute(manager: VKApiManager): List<Market> {
        val call = VKMethodCall.Builder()
            .method("market.get")
            .args("owner_id", "-$ownerId")
            .args("count", 200)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserMarkets())
    }
}

class FaveRequestAdd(
    private val ownerId: Int,
    private val marketId: Int
) : ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {
        val call = VKMethodCall.Builder()
            .method("fave.addProduct")
            .args("owner_id", ownerId)
            .args("id", marketId)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserFave())
    }
}

class FaveRequestDelete(
    private val ownerId: Int,
    private val marketId: Int
) : ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {
        val call = VKMethodCall.Builder()
            .method("fave.addProduct")
            .args("owner_id", "-$ownerId")
            .args("id", marketId)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserFave())
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

private class ResponseApiParserCities : VKApiResponseParser<List<City>> {
    override fun parse(response: String): List<City> {
        try {
            val items = JSONObject(response).getJSONObject("response")
                .getJSONArray("items")
            val cities = ArrayList<City>(items.length())
            for (i in 0 until items.length()) {
                val city = City.parse(items.getJSONObject(i))
                cities.add(city)
            }
            return cities
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ResponseApiParserMarkets : VKApiResponseParser<List<Market>> {
    override fun parse(response: String): List<Market> {
        try {
            val items = JSONObject(response).getJSONObject("response")
                .getJSONArray("items")
            val markets = ArrayList<Market>(items.length())
            for (i in 0 until items.length()) {
                val market = Market.parse(items.getJSONObject(i))
                markets.add(market)
            }
            return markets
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ResponseApiParserFave : VKApiResponseParser<Int> {
    override fun parse(response: String): Int {
        try {
            return JSONObject(response).optInt("response", 0)
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}