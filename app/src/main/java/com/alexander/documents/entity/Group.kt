package com.alexander.documents.entity

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * author alex
 */
data class Group(
    val id: Int = 0,
    var name: String = "",
    var description: String ="",
    val photo_50: String = "",
    val photo_100: String = "",
    val photo_200: String = "",
    val site: String = "",
    val city: String = "",
    val memberCount: Int = 0,
    val isClosed: Int = 0,
    var isSelected: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(photo_50)
        parcel.writeString(photo_100)
        parcel.writeString(photo_200)
        parcel.writeString(site)
        parcel.writeString(city)
        parcel.writeInt(memberCount)
        parcel.writeInt(isClosed)
        parcel.writeInt(isSelected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = Group(
            id = json.optInt("id", 0),
            name = json.optString("name", ""),
            description = json.optString("description", ""),
            photo_50 = json.optString("photo_50"),
            photo_100 = json.optString("photo_100"),
            photo_200 = json.optString("photo_200"),
            site = json.optString("site"),
            city = json.optJSONObject("city")?.optString("title") ?: "",
            memberCount = json.optInt("members_count"),
            isClosed = json.optInt("is_closed")
        )
    }
}