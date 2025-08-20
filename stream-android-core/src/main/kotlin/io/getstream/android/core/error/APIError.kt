/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.android.core.error

import com.squareup.moshi.Json

/**
 * Represents an API error response from the Stream API. This data class encapsulates all the error
 * information returned by the API when a request fails, providing detailed context about what went
 * wrong.
 *
 * Note: While this class can be generated from the OpenAPI specification, it is defined here to
 * allow usage across the different Stream products without the need to depend on OpenAPI codegen.
 *
 * @property code The specific error code identifying the type of error that occurred
 * @property duration The time duration it took to process the request before the error occurred
 * @property message A human-readable description of the error
 * @property moreInfo Additional information or documentation URL related to the error
 * @property statusCode The HTTP status code associated with the error response
 * @property details A list of additional error detail codes providing more context
 * @property unrecoverable Indicates whether this error is unrecoverable and the operation should
 *   not be retried. Null if not specified.
 * @property exceptionFields Additional key-value pairs providing extra context about the exception.
 *   Null if not provided.
 */
public data class APIError(
    @Json(name = "code") val code: Int,
    @Json(name = "duration") val duration: String,
    @Json(name = "message") val message: String,
    @Json(name = "more_info") val moreInfo: String,
    @Json(name = "StatusCode") val statusCode: Int,
    @Json(name = "details") val details: List<Int>,
    @Json(name = "unrecoverable") val unrecoverable: Boolean? = null,
    @Json(name = "exception_fields") val exceptionFields: Map<String, String>? = null,
)

/**
 * Container for API error responses. This class is used to wrap the [APIError] object, allowing for
 * easier deserialization.
 *
 * @property error The [APIError] object containing detailed error information.
 */
public data class APIErrorContainer(@Json(name = "error") val error: APIError)
