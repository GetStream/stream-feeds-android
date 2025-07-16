package io.getstream.android.core.http.interceptor

import io.getstream.android.core.user.ApiKey
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor adding the API key to the request.
 *
 * @param apiKey The API key to be added to the request headers.
 */
public class ApiKeyInterceptor(private val apiKey: ApiKey): Interceptor {

    private companion object {
        private const val API_KEY_PARAM = "api_key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the original request URL
        val request = chain.request()
        val url = request.url
        // Add the API key to the URL
        val urlWithApiKey = url.newBuilder()
            .addQueryParameter(API_KEY_PARAM, apiKey.value)
            .build()
        // Build the request with the new URL
        val requestWithApiKey = request.newBuilder()
            .url(urlWithApiKey)
            .build()
        // Proceed with the request
        return chain.proceed(requestWithApiKey)
    }
}