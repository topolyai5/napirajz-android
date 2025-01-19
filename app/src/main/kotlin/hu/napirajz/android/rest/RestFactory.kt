package hu.napirajz.android.rest

import com.squareup.moshi.Moshi
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

fun retrofit(
    serializerBuilder: Moshi.Builder = Serializer.moshiBuilder,
    converterFactory: Converter.Factory? = null,
): Retrofit {
    val baseUrl = "http://kereso.napirajz.hu"
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(serializerBuilder.build()))
        .apply {
            if (converterFactory != null) {
                addConverterFactory(converterFactory)
            }
        }.build()
}