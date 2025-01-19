package hu.napirajz.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hu.napirajz.android.extension.logError
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

class SearchImageViewModel : ViewModel() {
    private val _items =  MutableStateFlow<List<NapirajzData>>(emptyList())
    val items: StateFlow<List<NapirajzData>> = _items.asStateFlow()

    private val _query =  MutableStateFlow<String?>(null)
    val query: StateFlow<String?> = _query.asStateFlow()

    fun search() {
        _items.value = emptyList()
        viewModelScope.launch(Dispatchers.Default) {
            retrofit().create(NapirajzRest::class.java).search(_query.value)
                .subscribe(object : SingleObserver<Map<String, NapirajzData>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        logError(e.message?:"no error provided", e)
                    }

                    override fun onSuccess(t: Map<String, NapirajzData>) {
                        val totalItems = items.value.toMutableList()
                        totalItems.addAll(t.values)
                        _items.value = totalItems
                    }

                })
        }
    }

    fun setQueryString(it: String) {
        _query.value = it
    }
}

class SearchImageViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchImageViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}