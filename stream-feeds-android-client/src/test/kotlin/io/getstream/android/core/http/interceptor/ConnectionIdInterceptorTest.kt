package io.getstream.android.core.http.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Test

internal class ConnectionIdInterceptorTest {
    private var connectionId = ""

    private val interceptor = ConnectionIdInterceptor(::connectionId)

    @Test
    fun `intercept when connectionId is empty, then proceed with the same request`() {
        connectionId = ""
        val originalRequest = Request.Builder().url("http://getstream.io").build()
        val chain = mockChain(originalRequest)

        interceptor.intercept(chain)

        verify { chain.proceed(originalRequest) }
    }

    @Test
    fun `intercept when connectionId is not empty, then proceed with request with connectionId`() {
        connectionId = "12345"
        val originalRequest = Request.Builder().url("http://getstream.io").build()
        val expectedUrl = "http://getstream.io?connection_id=12345".toHttpUrl()
        val chain = mockChain(originalRequest)

        interceptor.intercept(chain)

        verify { chain.proceed(match { it.url == expectedUrl }) }
    }

    private fun mockChain(request: Request): Interceptor.Chain = mockk {
        every { request() } returns request
        every { proceed(any()) } returns mockk()
    }
}
