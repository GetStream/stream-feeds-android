package io.getstream.feeds.android.sample.utils

import android.util.Log

fun <T> Result<T>.logResult(tag: String, operation: String): Result<T> {
    fold(
        onSuccess = { Log.d(tag, "[Success] $operation") },
        onFailure = { Log.e(tag, "[Failure] $operation", it) }
    )
    return this
}
