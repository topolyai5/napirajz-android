package hu.napirajz.android.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import hu.napirajz.android.HistoryService
import hu.napirajz.android.R
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse
import hu.napirajz.android.rest.NapirajzRest
import kotlinx.android.synthetic.main.activity_swiper_rajz.*
import kotlinx.android.synthetic.main.activity_swiper_rajz.toolbar
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class SwiperRajzActivity : AppCompatActivity() {

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
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val historyService = HistoryService()

    var showShareDialog = false
    var showSearchDialog = false
    var searchText = ""
    var napiSearch = false
    var searctEdit: EditText? = null

    var resultSearch: ArrayList<NapirajzData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiper_rajz)

        setSupportActionBar(toolbar)
        setTitle(R.string.search_pic)
        toolbar.setTitleTextColor(Color.BLACK)

        if (savedInstanceState != null) {
            historyService.fromSaveInstance(savedInstanceState.getSerializable(RandomRajzActivity.NAPIRAJZ) as Stack<NapirajzData>)

            val resultSerializable = savedInstanceState.getSerializable(RandomRajzActivity.RESULT)
            if (resultSerializable != null) {
                resultSearch = resultSerializable as ArrayList<NapirajzData>
            }

            showSearchDialog = savedInstanceState.getBoolean(RandomRajzActivity.SHOW_SEARCH, false)
            showShareDialog = savedInstanceState.getBoolean(RandomRajzActivity.SHOW_SHARE, false)

            searchText = savedInstanceState.getString(RandomRajzActivity.SEARCH_TEXT, "")
        }
//
//        if (resultSearch != null && resultSearch!!.isNotEmpty()) {
//            showSearchResult(resultSearch!!)
//        }
//
//        if (historyService.isEmpty()) {
//            load(napirajzRest.random())
//        } else {
//            loadPicture()
//        }
//
        if (historyService.hasPrevious()) {
            toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_left_black_24dp)
        }
//
//        toolbar.setNavigationOnClickListener {
//            historyService.previous()
//            loadPicture()
//
//            if (!historyService.hasPrevious()) {
//                toolbar.navigationIcon = null
//            }
//        }
//
//        if (showSearchDialog) {
//            showSearchDialog()
//        }
//        if (showShareDialog) {
//            showShareDialog()
//        }



        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, 10)
        pager.adapter = pagerAdapter
    }
//
//    private fun loadPicture() {
//        scrollToTop()
//        title = historyService.current().cim
//        titleText.text = historyService.current().cim + " - " + dateFormatter.format(historyService.current().datum)
//        val dm = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(dm)
//        Log.w("asd", historyService.current().url)
//        if (historyService.current().egyeb.isEmpty()) {
//            intro.visibility = View.GONE
//        } else {
//            intro.text = historyService.current().egyeb
//            intro.visibility = View.VISIBLE
//        }
//        nextImage.isEnabled = true
//
//    }
//
//    fun scrollToTop() {
//        scrollView.scrollTo(0, 0)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val daily = menu.add(Menu.NONE, RandomRajzActivity.dailyId, Menu.NONE, "Napi")
        daily.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        daily.icon = ContextCompat.getDrawable(this, R.drawable.e_question)

        val share = menu.add(Menu.NONE, RandomRajzActivity.shareId, Menu.NONE, "Megoszt")
        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        share.icon = ContextCompat.getDrawable(this, R.drawable.ic_share_black_36dp)

        val search = menu.add(Menu.NONE, RandomRajzActivity.searchId, Menu.NONE, "Abort")
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        search.icon = ContextCompat.getDrawable(this, R.drawable.ic_search_black_24dp)

        return true
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, val pages: Long) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = pages.toInt()

        override fun getItem(position: Int): Fragment {
            val fragment = ScreenSlidePageFragment().apply {
                arguments = Bundle().apply {
                    putLong("data", Gson().toJson())
                }
            }
            return fragment
        }
    }
}