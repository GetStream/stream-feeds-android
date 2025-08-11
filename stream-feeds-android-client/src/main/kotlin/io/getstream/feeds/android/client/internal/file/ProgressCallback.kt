package io.getstream.feeds.android.client.internal.file

/**
 * Callback to listen for file upload progress.
 */
internal fun interface ProgressCallback {
    /**
     * Called when the attachment upload is in progress.
     */
    fun onProgress(uploaded: Long, total: Long)
}
