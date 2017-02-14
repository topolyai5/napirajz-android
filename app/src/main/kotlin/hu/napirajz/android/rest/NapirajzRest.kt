package hu.napirajz.android.rest

import hu.napirajz.android.response.NapirajzResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface NapirajzRest {

    @GET("abort.php?guppi&json")
    fun random(): Observable<NapirajzResponse>

    @GET("abort.php")
    fun daily(@Query("tol") tol: String, @Query("ig") ig: String, @Query("n") n: Int = 1, @Query("json") json: String = ""): Observable<NapirajzResponse>

}
