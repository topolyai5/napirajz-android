package hu.napirajz.android.activity

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import com.google.gson.GsonBuilder
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import hu.napirajz.android.HistoryService
import hu.napirajz.android.R
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse
import hu.napirajz.android.rest.NapirajzRest
import hu.napirajz.android.serializer.NapirajzDeserializer
import hu.napirajz.android.transformation.HeightWrapBitmapTarget
import kotlinx.android.synthetic.main.activity_random_rajz.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.dip
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick
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

class RandomRajzActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        val NAPIRAJZ = "napirajz"
        val RESULT = "result"
        val SHOW_SEARCH = "search"
        val SEARCH_TEXT = "search_text"
        val SHOW_SHARE = "share"
        val dailyId = 1
        val shareId = 2
        val searchId = 3
        val BASE_URL = "http://kereso.napirajz.hu/"
    }

    lateinit var napirajzRest: NapirajzRest
    lateinit var picasso: Picasso
    val historyService = HistoryService()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    var showShareDialog = false
    var showSearchDialog = false
    var searchText = ""
    var napiSearch = false
    var searctEdit: EditText? = null

    var resultSearch: ArrayList<NapirajzData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_rajz)

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
                .baseUrl(BASE_URL)
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
            historyService.fromSaveInstance(savedInstanceState.getSerializable(NAPIRAJZ) as Stack<NapirajzData>)

            val resultSerializable = savedInstanceState.getSerializable(RESULT)
            if (resultSerializable != null) {
                resultSearch = resultSerializable as ArrayList<NapirajzData>
            }

            showSearchDialog = savedInstanceState.getBoolean(SHOW_SEARCH, false)
            showShareDialog = savedInstanceState.getBoolean(SHOW_SHARE, false)

            searchText = savedInstanceState.getString(SEARCH_TEXT, "")
        }

        if (resultSearch != null && resultSearch!!.isNotEmpty()) {
            showSearchResult(resultSearch!!)
        }

        if (historyService.isEmpty()) {
            load(napirajzRest.random())
        } else {
            loadPicture()
        }

        if (historyService.hasPrevious()) {
            toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_left_black_24dp)
        }

        toolbar.setNavigationOnClickListener {
            historyService.previous()
            loadPicture()

            if (!historyService.hasPrevious()) {
                toolbar.navigationIcon = null
            }
        }

        if (showSearchDialog) {
            showSearchDialog()
        }
        if (showShareDialog) {
            showShareDialog()
        }

        refreshImage.setOnRefreshListener(this)
//        imageView.setOnTouchListener(ZoomInZoomOut())

    }

    private fun load(observable: Observable<NapirajzResponse>) {
        refreshImage.isRefreshing = false
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
                            historyService.add(t.data[0])
                            if (historyService.hasPrevious()) {
                                toolbar.navigationIcon = ContextCompat.getDrawable(this@RandomRajzActivity, R.drawable.ic_keyboard_arrow_left_black_24dp)
                            }
                            loadPicture()
                            imageView.onLongClick {
                                if (historyService.current().lapUrl.isNotEmpty()) {
                                    Toast.makeText(this@RandomRajzActivity, "Kösz Tibi/Klára!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent()
                                    intent.action = Intent.ACTION_VIEW
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                                    intent.data = Uri.parse(historyService.current().lapUrl)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@RandomRajzActivity, "Ez csak kép (lehet, hogy egy borító). Bocs.", Toast.LENGTH_SHORT).show()
                                }
                                true
                            }

                            imageView.onClick {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Napirajz", historyService.current().url);
                                clipboard.setPrimaryClip(clip);
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
        title = historyService.current().cim
        titleText.text = historyService.current().cim + " - " + dateFormatter.format(historyService.current().datum)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        Log.w("asd", historyService.current().url)
        if (historyService.current().egyeb.isEmpty()) {
            intro.visibility = View.GONE
        } else {
            intro.text = historyService.current().egyeb
            intro.visibility = View.VISIBLE
        }
        nextImage.isEnabled = true
        target = HeightWrapBitmapTarget(dm.widthPixels, imageView, nextImageLoader)
        picasso.load(historyService.current().url)
                .placeholder(R.drawable.napirajz_logo48)
                .into(target)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(NAPIRAJZ, historyService.forSaveInstance())
        if (resultSearch != null) {
            outState.putSerializable(RESULT, resultSearch)
        } else {
            outState.remove(RESULT)
        }

        outState.putBoolean(SHOW_SHARE, showShareDialog)
        outState.putBoolean(SHOW_SEARCH, showSearchDialog)

        if (searctEdit != null) {
            outState.putString(SEARCH_TEXT, searctEdit!!.text.toString())
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val daily = menu.add(Menu.NONE, dailyId, Menu.NONE, "Napi")
        daily.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        daily.icon = ContextCompat.getDrawable(this, R.drawable.e_question)

        val share = menu.add(Menu.NONE, shareId, Menu.NONE, "Megoszt")
        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        share.icon = ContextCompat.getDrawable(this, R.drawable.ic_share_black_36dp)

        val search = menu.add(Menu.NONE, searchId, Menu.NONE, "Abort")
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        search.icon = ContextCompat.getDrawable(this, R.drawable.ic_search_black_24dp)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            dailyId -> {
                val format = dateFormatter.format(Date())
                napiSearch = true
                load(napirajzRest.daily(format, format))
                return true
            }
            shareId -> {
                showShareDialog()
                showShareDialog = true
                showSearchDialog = false
                return true
            }
            searchId -> {
                showSearchDialog()
                showSearchDialog = true
                showShareDialog = false
                return true
            }
        }
        return false
    }

    private fun showShareDialog() {
        AlertDialog.Builder(this)
                .setMessage("Melyiket akarod megosztani?")
                .setPositiveButton("Csak kép", { dialogInterface, i ->
                    share(historyService.current().url, "text/plain")
                    showSearchDialog = false
                })
                .setNeutralButton("A szájt", { dialogInterface, i ->
                    share(historyService.current().lapUrl, "text/plain")
                    showSearchDialog = false
                })
                .setOnDismissListener {
                    showShareDialog= false
                }
                .show()
    }

    private fun showSearchDialog() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.search_dialog, null)
        searctEdit = view.findViewById(R.id.searchId) as EditText
        searctEdit!!.setText(searchText)
        AlertDialog.Builder(this)
                .setView(view)
                .setNeutralButton("Téves", null)
                .setPositiveButton("Abort", { dialogInterface, i ->
                    val editText = (dialogInterface as Dialog).findViewById(R.id.searchId) as EditText
                    if (editText.text.isEmpty()) {
                        Toast.makeText(this@RandomRajzActivity, "Ne bassz fel Tibi... Legalább 4!", Toast.LENGTH_SHORT).show()
                    } else {
                        search(editText.text.toString())
                    }
                })
                .setOnDismissListener {
                    searchText = ""
                    showSearchDialog = false
                }
                .show()
    }

    fun scrollToTop() {
        scrollView.scrollTo(0, 0)
    }

    fun share(extra: String, mimeType: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, extra)
        sendIntent.type = mimeType
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
    }

    override fun onRefresh() {
        load(napirajzRest.random())
    }

    fun search(text: String) {
        napirajzRest.search(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<NapirajzResponse> {
                    override fun onError(e: Throwable?) {
                        Toast.makeText(this@RandomRajzActivity, R.string.failed, Toast.LENGTH_SHORT).show()
                        setTitle(R.string.failed)
                        e!!.printStackTrace()
                    }

                    override fun onCompleted() {

                    }

                    override fun onNext(t: NapirajzResponse) {
                        showSearchResult(ArrayList(t.data))
                        resultSearch = t.data
                    }

                })
    }

    private fun showSearchResult(data: ArrayList<NapirajzData>) {
        if (data.isEmpty()) {
            Toast.makeText(this@RandomRajzActivity, "Szultán nem találta...", Toast.LENGTH_SHORT).show()
            return
        }
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.search_result_layout, null)
        val listview = view.findViewById(R.id.searchResultListview) as ListView
        val adapter = object : ArrayAdapter<NapirajzData>(this, R.layout.result_item_layout, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var v = convertView
                if (convertView == null) {
                    v = inflater.inflate(R.layout.result_item_layout, null)
                }

                val item = getItem(position)
                (v!!.findViewById(R.id.resultTitle) as TextView).text = item.cim

                picasso.load(item.url)
                        .placeholder(R.drawable.e_question)
                        .resize(dip(80), dip(80))
                        .centerCrop()
                        .into(v.findViewById(R.id.resultImage) as ImageView)

                return v
            }
        }

        val dialog = AlertDialog.Builder(this)
                .setView(view)
                .setOnDismissListener {
                    resultSearch = null
                }
                .show()

        listview.setOnItemClickListener { adapterView, view, i, l ->
            val item = adapter.getItem(i)
            historyService.add(item)
            loadPicture()
            dialog.dismiss()
            nextImageLoader.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            if (historyService.hasPrevious()) {
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_left_black_24dp)
            }
        }

        listview.adapter = adapter
    }
}