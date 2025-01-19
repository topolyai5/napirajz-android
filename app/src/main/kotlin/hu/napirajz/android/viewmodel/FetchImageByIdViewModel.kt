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

class FetchImageByIdViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<NapirajzData>>(emptyList())
    val items: StateFlow<List<NapirajzData>> = _items.asStateFlow()

    private val _id = MutableStateFlow<Long?>(null)
    val id: StateFlow<Long?> = _id.asStateFlow()

    fun fetch() {
        val id = id.value ?: return
        viewModelScope.launch(Dispatchers.Default) {
            retrofit().create(NapirajzRest::class.java).getById(id)
                .subscribe(object : SingleObserver<Map<String, NapirajzData>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        logError(e.message ?: "no error provided", e)
                    }

                    override fun onSuccess(t: Map<String, NapirajzData>) {
                        val totalItems = items.value.toMutableList()
                        totalItems.addAll(t.values)
                        _items.value = totalItems
                    }
                })
        }
    }

    fun update(id: Long) {
        if (_id.value != id) {
            _items.value = emptyList()
            _id.value = id
            fetch()
        }
    }
}

class FetchImageByIdViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchImageByIdViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchImageByIdViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}