package com.alexander.documents.entity

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * author alex
 */
data class Album(
    val id: Int = 0,
    val thumbId: Int = 0,
    val title: String = "",
    val description: String = "",
    val size: Int = 0,
    val photoUrl: String = "",
    var isSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readByte() == 1.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(thumbId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(size)
        parcel.writeString(photoUrl)
        parcel.writeByte(if (isSelected) 1.toByte() else 0.toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album {
            return Album(parcel)
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = Album(
            id = json.optInt("id", 0),
            thumbId = json.optInt("thumbId", 0),
            title = json.optString("title", ""),
            description = json.optString("description", ""),
            photoUrl = json.optString("thumb_src", ""),
            size = json.optInt("size", 0)
        )
    }
}