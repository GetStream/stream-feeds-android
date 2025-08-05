package io.getstream.feeds.android.client.internal.http.interceptor

import io.getstream.android.core.error.APIError
import io.getstream.feeds.android.client.api.error.StreamApiException
import io.getstream.android.core.parser.JsonParser
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that handles API errors by parsing the response body and throwing a
 * [StreamApiException] if the response is not successful.
 *
 * @param jsonParser The JSON parser to parse the error response.
 */
internal class ApiErrorInterceptor(private val jsonParser: JsonParser): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            // Try to parse a Stream API error from the response body
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            jsonParser.fromJsonOrError(errorBody, APIError::class.java)
                .map { apiError ->
                    throw StreamApiException(apiError)
                }
        }
        return response
    }
}
