package io.getstream.feeds.android.client.internal.http

import io.getstream.feeds.android.client.internal.file.ProgressCallback
import io.mockk.mockk
import io.mockk.verify
import okhttp3.RequestBody
import okio.BufferedSink
import org.junit.Test

internal class ProgressRequestBodyTest {
    private val delegate: RequestBody = TestRequestBody(1000)
    private val callback: ProgressCallback = mockk(relaxed = true)

    private val progressRequestBody = ProgressRequestBody(
        delegate = delegate,
        callback = callback
    )

    @Test
    fun `on writeTo, write to delegate and notify progress`() {
        val sink: BufferedSink = mockk(relaxed = true)

        progressRequestBody.writeTo(sink)

        verify {
            sink.write(any(), any())
            callback.onProgress(uploaded = 1000, total = 1000)
        }
    }

    private class TestRequestBody(private val length: Int) : RequestBody() {
        override fun contentType() = null
        override fun contentLength() = length.toLong()
        override fun writeTo(sink: BufferedSink) {
            sink.write(ByteArray(length), 0, length)
        }
    }
}
