package hu.napirajz.android.activity

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import hu.napirajz.android.HistoryService
import hu.napirajz.android.R
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse
import hu.napirajz.android.rest.NapirajzRest
import hu.napirajz.android.transformation.HeightWrapBitmapTarget
import kotlinx.android.synthetic.main.activity_random_rajz.*
import kotlinx.android.synthetic.main.fragment_screen_slide_page.view.*
import kotlinx.android.synthetic.main.fragment_screen_slide_page.view.imageView
import okhttp3.OkHttpClient
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ScreenSlidePageFragment : Fragment() {

    lateinit var picasso: Picasso

    lateinit var target: Target

    val historyService = HistoryService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_screen_slide_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val client = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        picasso = Picasso.Builder(activity)
                .downloader(OkHttp3Downloader(client))
                .listener { picasso, uri, exception ->
                    Log.e("RandomRajz", "Cannot load picture: $uri", exception)
                }
                .build()
        val url = arguments?.getString("url", "")!!
        target = HeightWrapBitmapTarget(dm.widthPixels, imageView, nextImageLoader)
        picasso.load(url)
                .placeholder(R.drawable.napirajz_logo48)
                .into(target)
    }

    private fun load(observable: Observable<NapirajzResponse>) {
//            if (!napiSearch) {
//                nextImageLoader.visibility = View.VISIBLE
//                imageView.visibility = View.GONE
//            }

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<NapirajzResponse> {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: NapirajzResponse?) {
                        if (t != null) {
//                                nextImageLoader.visibility = View.VISIBLE
//                                imageView.visibility = View.GONE
                            historyService.add(t.data[0])
                            if (historyService.hasPrevious()) {
//                                    toolbar.navigationIcon = ContextCompat.getDrawable(this@RandomRajzActivity, R.drawable.ic_keyboard_arrow_left_black_24dp)
                            }
                            loadPicture()
//                                imageView.onLongClick {
//                                    if (historyService.current().lapUrl.isNotEmpty()) {
//                                        Toast.makeText(this@RandomRajzActivity, "Kösz Tibi/Klára!", Toast.LENGTH_SHORT).show()
//                                        val intent = Intent()
//                                        intent.action = Intent.ACTION_VIEW
//                                        intent.addCategory(Intent.CATEGORY_BROWSABLE)
//                                        intent.data = Uri.parse(historyService.current().lapUrl)
//                                        startActivity(intent)
//                                    } else {
//                                        Toast.makeText(this@RandomRajzActivity, "Ez csak kép (lehet, hogy egy borító). Bocs.", Toast.LENGTH_SHORT).show()
//                                    }
//                                    true
//                                }

//                                imageView.onClick {
//                                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                                    val clip = ClipData.newPlainText("Napirajz", historyService.current().url);
//                                    clipboard.setPrimaryClip(clip);
//                                }

                        } else {
//                            setTitle(R.string.failed)
                        }
//                            nextImage.isEnabled = true
//                        napiSearch = false
                    }

                    override fun onError(e: Throwable?) {
//                            if (!napiSearch) {
//                                Toast.makeText(this@RandomRajzActivity, R.string.failed, Toast.LENGTH_SHORT).show()
//                                setTitle(R.string.failed)
//                            } else {
//                                Toast.makeText(this@RandomRajzActivity, R.string.daily_not_found, Toast.LENGTH_SHORT).show()
//                            }

//                            nextImageLoader.visibility = View.GONE
//                            imageView.visibility = View.VISIBLE
//                            nextImage.isEnabled = true
//                        napiSearch = false
                    }
                })
    }

    private fun loadPicture() {
        scrollToTop()
//        title = historyService.current().cim
//        titleText.text = historyService.current().cim + " - " + dateFormatter.format(historyService.current().datum)
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        Log.w("asd", historyService.current().url)
        if (historyService.current().egyeb.isEmpty()) {
            intro.visibility = View.GONE
        } else {
            intro.text = historyService.current().egyeb
            intro.visibility = View.VISIBLE
        }
        nextImage.isEnabled = true

    }

    fun scrollToTop() {
        scrollView.scrollTo(0, 0)
    }
}
