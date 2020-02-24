package com.alexander.documents.entity

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * author alex
 */
data class Market(
    val id: Int = 0,
    val ownerId: Int = 0,
    val title: String = "",
    val description: String ="",
    val price: Price? = null,
    val photo: String = "",
    val isFavorite: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable<Price>(Price::class.java.classLoader),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(ownerId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeParcelable(price, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
        parcel.writeString(photo)
        parcel.writeByte((if (isFavorite) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Market> {
        override fun createFromParcel(parcel: Parcel): Market {
            return Market(parcel)
        }

        override fun newArray(size: Int): Array<Market?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = Market(
            id = json.optInt("id", 0),
            ownerId = json.optInt("owner_id", 0),
            title = json.optString("title", ""),
            description = json.optString("description", ""),
            price = Price.parse(json.getJSONObject("price")),
            photo = json.optString("thumb_photo", ""),
            isFavorite = json.optBoolean("is_favorite", false)
        )
    }
}