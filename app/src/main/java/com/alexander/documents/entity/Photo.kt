package com.alexander.documents.entity

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class Photo(
    val id: Int = 0,
    val url: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = Photo(
            id = json.optInt("id", 0),
            url = json.getJSONArray("sizes").getJSONObject(0).optString("url", "")
        )
    }
}