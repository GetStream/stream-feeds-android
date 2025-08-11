package io.getstream.feeds.android.client.internal.http

import io.getstream.feeds.android.client.internal.file.ProgressCallback
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer

internal class ProgressRequestBody(
    private val delegate: RequestBody,
    private val callback: ProgressCallback,
) : RequestBody() {

    override fun contentType(): MediaType? = delegate.contentType()
    override fun contentLength(): Long = delegate.contentLength()

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink).buffer()
        delegate.writeTo(countingSink)
        countingSink.flush()
    }

    private inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private val total = contentLength()
        private var uploaded = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            uploaded += byteCount

            callback.onProgress(uploaded, total)
        }
    }
}
