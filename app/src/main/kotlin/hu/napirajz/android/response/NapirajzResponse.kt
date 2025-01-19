package hu.napirajz.android.response

import java.io.Serializable

data class NapirajzResponse(val data: List<NapirajzData>) : Serializable
