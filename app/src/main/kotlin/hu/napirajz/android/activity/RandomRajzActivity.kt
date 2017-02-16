package hu.napirajz.android.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import hu.napirajz.android.*
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse
import hu.napirajz.android.rest.NapirajzRest
import kotlinx.android.synthetic.main.activity_random_rajz.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RandomRajzActivity : AppCompatActivity() {
    lateinit var napirajzRest: NapirajzRest
    lateinit var picasso: Picasso

    var napiSearch = false

    private var lastNapirajzData: NapirajzData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_rajz)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        setTitle(R.string.search_pic)
        toolbar.setTitleTextColor(Color.BLACK)

        val client = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        picasso = Picasso.Builder(this)
                .downloader(OkHttp3Downloader(client))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory
                        .create(GsonBuilder()
                                .registerTypeAdapter(NapirajzResponse::class.java, NapirajzDeserializer())
                                .setDateFormat("yyyy-MM-dd")
                                .create()))
                .build()

        napirajzRest = retrofit.create(NapirajzRest::class.java)

        nextImage.setOnClickListener {
            load(napirajzRest.random())
            napiSearch = false
        }

        if (savedInstanceState != null) {
            lastNapirajzData = savedInstanceState.getSerializable(NAPIRAJZ) as NapirajzData
        }
        if (lastNapirajzData == null) {
            load(napirajzRest.random())
        } else {
            loadPicture()
        }

//        imageView.setOnTouchListener(ZoomInZoomOut())

    }

    private fun load(observable: Observable<NapirajzResponse>) {

        //disable on every request
        nextImage.isEnabled = false
        //if it's daily image, we should show load bar later time
        if (!napiSearch) {
            nextImageLoader.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        }

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<NapirajzResponse> {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: NapirajzResponse?) {
                        if (t != null) {
                            nextImageLoader.visibility = View.VISIBLE
                            imageView.visibility = View.GONE
                            lastNapirajzData = t.data
                            loadPicture()
                            imageView.onClick {
                                if (lastNapirajzData!!.lapUrl.isNotEmpty()) {
                                    Toast.makeText(this@RandomRajzActivity, "Kösz Tibi/Klára!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent()
                                    intent.action = Intent.ACTION_VIEW
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                                    intent.data = Uri.parse(lastNapirajzData!!.lapUrl)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@RandomRajzActivity, "Ez csak kép (lehet, hogy egy borító). Bocs.", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            setTitle(R.string.failed)
                        }
                        nextImage.isEnabled = true
                        napiSearch = false
                    }

                    override fun onError(e: Throwable?) {
                        if (!napiSearch) {
                            Toast.makeText(this@RandomRajzActivity, R.string.failed, Toast.LENGTH_SHORT).show()
                            setTitle(R.string.failed)
                        } else {
                            Toast.makeText(this@RandomRajzActivity, R.string.daily_not_found, Toast.LENGTH_SHORT).show()
                        }

                        nextImageLoader.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        nextImage.isEnabled = true
                        napiSearch = false
                    }
                })
    }

    lateinit var target: Target
    private fun loadPicture() {
        scrollToTop()
        title = lastNapirajzData!!.cim
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        Log.w("asd", lastNapirajzData!!.url)
        if (lastNapirajzData!!.egyeb.isEmpty()) {
            intro.visibility = View.GONE
        } else {
            intro.text = lastNapirajzData!!.egyeb
            intro.visibility = View.VISIBLE
        }
        nextImage.isEnabled = true
        target = HeightWrapBitmapTarget(dm.widthPixels, imageView, nextImageLoader)
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
        val dailyId = 1
        val shareId = 2
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val daily = menu.add(Menu.NONE, dailyId, Menu.NONE, "Napi")
        daily.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        daily.icon = ContextCompat.getDrawable(this, R.drawable.e_question)

        val share = menu.add(Menu.NONE, shareId, Menu.NONE, "Megoszt")
        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        share.icon = ContextCompat.getDrawable(this, R.drawable.ic_share_black_36dp)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            dailyId -> {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                napiSearch = true
                load(napirajzRest.daily(format, format))
                return true
            }
            shareId -> {
                AlertDialog.Builder(this)
                        .setMessage("Melyiket akarod megosztani?")
                        .setPositiveButton("Csak kép", { dialogInterface, i ->
                            share(lastNapirajzData!!.url, "image/jpeg")
                        })
                        .setNeutralButton("A weboldalt", { dialogInterface, i ->
                            share(lastNapirajzData!!.lapUrl, "text/plain")
                        })
                        .show()
                return true
            }
        }
        return false
    }

    fun scrollToTop() {
//        val params = appbarLayout.layoutParams as CoordinatorLayout.LayoutParams
//        val behavior = params.getBehavior() as AppBarLayout.Behavior?
//        if (behavior != null) {
//            behavior.onNestedPreScroll(activity_random_rajz, appbarLayout, scrollView, 0, 0, IntArray(2))
//        }

        scrollView.scrollTo(0, 0)
    }

    fun share(extra: String, mimeType: String) {
        var sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, extra)
        sendIntent.type = mimeType
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
    }

}