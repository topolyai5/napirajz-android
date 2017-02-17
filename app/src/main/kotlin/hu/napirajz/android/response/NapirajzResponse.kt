package hu.napirajz.android.response

import java.io.Serializable
import java.util.*

data class NapirajzResponse(val data: ArrayList<NapirajzData>) : Serializable
