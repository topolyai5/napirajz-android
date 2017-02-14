package hu.napirajz.android.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import hu.napirajz.android.Const
import hu.napirajz.android.HeightWrapBitmapTarget
import hu.napirajz.android.NapirajzDeserializer
import hu.napirajz.android.R
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse
import hu.napirajz.android.rest.NapirajzRest
import kotlinx.android.synthetic.main.activity_random_rajz.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RandomRajzActivity : AppCompatActivity() {
    lateinit var napirajzRest: NapirajzRest
    lateinit var picasso: Picasso
    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var nextPic: FloatingActionButton

    private var lastNapirajzData: NapirajzData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_rajz)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        setTitle(R.string.search_pic)

        val client = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        picasso = Picasso.Builder(this)
                .downloader(OkHttp3Downloader(client))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory
                        .create(GsonBuilder()
                                .registerTypeAdapter(NapirajzResponse::class.java, NapirajzDeserializer())
                                .setDateFormat("yyyy-MM-dd")
                                .create()))
                .build()

        napirajzRest = retrofit.create(NapirajzRest::class.java)

        imageView = find<ImageView>(R.id.napirajz_imageview)
        progressBar = find<ProgressBar>(R.id.napirajz_loader)
        nextPic = find<FloatingActionButton>(R.id.next_pic)

        nextPic.setOnClickListener { load() }

        if (savedInstanceState != null) {
            lastNapirajzData = savedInstanceState.getSerializable(NAPIRAJZ) as NapirajzData
        }
        if (lastNapirajzData == null) {
            load()
        } else {
            loadPicture()
        }
    }

    private fun load() {

        nextPic.isEnabled = false
        progressBar.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        napirajzRest.random()
                .enqueue(object : Callback<NapirajzResponse> {
                    override fun onResponse(call: Call<NapirajzResponse>, response: Response<NapirajzResponse>) {
                        if (response.isSuccessful) {
                            lastNapirajzData = response.body().data
                            loadPicture()
                        } else {
                            setTitle(R.string.failed)
                        }

                    }

                    override fun onFailure(call: Call<NapirajzResponse>, t: Throwable) {
                        Toast.makeText(this@RandomRajzActivity, t.message, Toast.LENGTH_SHORT).show()
                        t.printStackTrace()
                        progressBar.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        nextPic.isEnabled = true
                        setTitle(R.string.failed)
                    }
                })
    }

    lateinit var target: Target
    private fun loadPicture() {
        scrollView.scrollTo(0, 0)
        title = lastNapirajzData!!.cim
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        Log.w("asd", lastNapirajzData!!.url)
        nextPic.isEnabled = true
        target = HeightWrapBitmapTarget(dm.widthPixels, imageView, progressBar)
        picasso.load(lastNapirajzData!!.url)
                .placeholder(R.drawable.napirajz_logo48)
                .into(target)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(NAPIRAJZ, lastNapirajzData)
    }

    companion object {
        val NAPIRAJZ = "napirajz"
    }

}