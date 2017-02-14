package hu.napirajz.android.rest

import hu.napirajz.android.response.NapirajzResponse
import retrofit2.Call
import retrofit2.http.GET

interface NapirajzRest {

    @GET("abort.php?guppi&json")
    fun random(): Call<NapirajzResponse>

}
