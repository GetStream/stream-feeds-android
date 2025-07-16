package io.getstream.android.core.http.interceptor

import io.getstream.android.core.http.XStreamClient
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor which adds the default headers to the request.
 *
 * @property xStreamClient the value of the `X-Stream-Client` header.
 */
public class HeadersInterceptor(private val xStreamClient: XStreamClient): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestWithHeaders = request.newBuilder()
            .addHeader(HEADER_X_STREAM_CLIENT, xStreamClient.value)
            .build()
        return chain.proceed(requestWithHeaders)
    }

    private companion object {
        private const val HEADER_X_STREAM_CLIENT = "X-Stream-Client"
    }
}
