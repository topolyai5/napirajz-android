package hu.napirajz.android.rest

import hu.napirajz.android.response.NapirajzResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NapirajzRest {

    @GET("abort.php?guppi&json")
    fun random(): Single<NapirajzResponse>

    @GET("abort.php")
    fun daily(@Query("tol") tol: String, @Query("ig") ig: String, @Query("n") n: Int = 1, @Query("json") json: String = ""): Single<NapirajzResponse>

    @POST("abort.php")
    fun search(@Query("q") q: String, @Query("json") json: String = ""): Single<NapirajzResponse>

}
