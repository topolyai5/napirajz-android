package hu.napirajz.android.response

import com.squareup.moshi.Json
import java.io.Serializable
import java.time.LocalDate

data class NapirajzData(
        @Json(name = "ID")
        val id: String,
        @Json(name = "Cim")
        val cim: String,
        @Json(name = "Datum")
        val datum: LocalDate,
        @Json(name = "URL")
        val url: String,
        @Json(name = "LapURL")
        val lapUrl: String?,
        @Json(name = "Parbeszed")
        val parbeszed: String,
        @Json(name = "Egyeb")
        val egyeb: String
) : Serializable
