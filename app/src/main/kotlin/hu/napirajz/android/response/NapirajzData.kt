package hu.napirajz.android.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class NapirajzData(
        @SerializedName("ID")
        val id: String,
        @SerializedName("Cim")
        val cim: String,
        @SerializedName("Datum")
        val datum: Date,
        @SerializedName("URL")
        val url: String,
        @SerializedName("LapURL")
        val lapUrl: String,
        @SerializedName("Parbeszed")
        val parbeszed: String,
        @SerializedName("Egyeb")
        val egyeb: String
) : Serializable
