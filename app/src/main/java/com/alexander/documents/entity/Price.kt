package com.alexander.documents.entity

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * author alex
 */
data class Price(
    val amount: String = "",
    val currency: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Price> {
        override fun createFromParcel(parcel: Parcel): Price {
            return Price(parcel)
        }

        override fun newArray(size: Int): Array<Price?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = Price(
            amount = json.optString("amount",  ""),
            currency = currencyMatcher(json.getJSONObject("currency").optInt("id", 0))
        )

        private fun currencyMatcher(currencyCode: Int): String {
            return when (currencyCode) {
                840 -> "$"
                978 -> "EUR"
                643 -> "Р"
                else -> ""
            }
        }
    }
}