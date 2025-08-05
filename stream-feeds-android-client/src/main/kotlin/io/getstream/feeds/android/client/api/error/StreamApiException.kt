package io.getstream.feeds.android.client.api.error

import io.getstream.android.core.error.APIError
import java.io.IOException

/**
 * Exception thrown when an error occurs while interacting with the Stream Feeds API.
 *
 * @param message The detail message of the exception.
 * @param cause The cause of the exception, if any.
 * @param apiError The API error details, if available.
 */
public class StreamApiException(public val apiError: APIError): IOException()
