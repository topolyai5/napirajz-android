package hu.napirajz.android.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hu.napirajz.android.extension.logError
import hu.napirajz.android.extension.logInfo
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.rest.NapirajzRest
import hu.napirajz.android.rest.retrofit
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoadImageViewModel(val lazyListState: LazyListState) : ViewModel() {
    private val _items =  MutableStateFlow<List<NapirajzData>>(emptyList())
    val items: StateFlow<List<NapirajzData>> = _items.asStateFlow()

    private val _view =  MutableStateFlow("list")
    val view: StateFlow<String> = _view.asStateFlow()

    fun getNewRandom(count: Long = 1) {
        viewModelScope.launch(Dispatchers.Default) {
            for (i in 0 until count) {
                retrofit().create(NapirajzRest::class.java).random()
                    .subscribe(object : SingleObserver<Map<String, NapirajzData>> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            logError(e.message?:"no error provided", e)
                        }

                        override fun onSuccess(t: Map<String, NapirajzData>) {
                            val totalItems = items.value.toMutableList()
                            if (t.values.size > 1) {
                                logInfo("Result more than 1: size: ${t.values.size}, ${t.values.map { it.cim }}", "LoadImageViewModel.getNewRandom")
                            }
                            val item = t.values.firstOrNull { !it.lapUrl.isNullOrBlank() } ?: t.values.firstOrNull()
                            if (item != null) {
                                totalItems.add(item)
                                _items.value = totalItems
                            }
                        }

                    })
            }
        }
    }

    fun changeView(view: String) {
        _view.value = view
    }

}

class LoadImageViewModelFactory(val listState: LazyListState): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoadImageViewModel(listState) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}