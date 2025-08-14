package io.getstream.feeds.android.sample.util

sealed interface AsyncResource<out T> {
    data object Loading : AsyncResource<Nothing>
    data object Error : AsyncResource<Nothing>
    data class Content<T>(val data: T) : AsyncResource<T>

    companion object
}

fun <T : Any> AsyncResource.Companion.notNull(data: T?) = when (data) {
    null -> AsyncResource.Error
    else -> AsyncResource.Content(data)
}

inline fun <T : Any, R : Any> AsyncResource<T>.map(transform: (T) -> R): AsyncResource<R> = when (this) {
    is AsyncResource.Loading -> AsyncResource.Loading
    is AsyncResource.Error -> AsyncResource.Error
    is AsyncResource.Content -> AsyncResource.Content(transform(data))
}
