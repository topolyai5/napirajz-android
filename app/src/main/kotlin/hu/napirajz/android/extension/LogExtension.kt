package hu.napirajz.android.extension

import android.util.Log
import hu.napirajz.android.BuildConfig

fun logInfo(message: String, tag: String = "TAG", justDebug: Boolean = true) {
    if (justDebug && BuildConfig.DEBUG) {
        Log.i(tag, message)
    }
    if (!justDebug) {
        Log.i(tag, message)
    }
}

fun logError(message: String, e: Throwable? = null, tag: String = "TAG", justDebug: Boolean = true) {
    if (justDebug && BuildConfig.DEBUG) {
        Log.e(tag, message, e)
    }
    if (!justDebug) {
        Log.e(tag, message, e)
    }
}
