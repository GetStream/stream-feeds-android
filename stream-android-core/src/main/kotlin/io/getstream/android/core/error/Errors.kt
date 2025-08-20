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

/**
 * List of error codes that indicate an invalid token error.
 * - 40: Authentication Token Expired
 * - 41: Authentication Token Not Valid Yet
 * - 42: Authentication Token Before Issued At
 *
 * For more details, refer to
 * [API Error Codes](https://getstream.io/chat/docs/android/api_errors_response/).
 */
internal val TokenInvalidErrorCodes = listOf(40, 41, 42)

/**
 * Determines if this [APIError] represents a token invalid error.
 *
 * @return `true` if the error code is one of the token invalid error codes (40, 41, 42), `false`
 *   otherwise.
 */
public val APIError.isTokenInvalidErrorCode: Boolean
    get() = code in TokenInvalidErrorCodes

/**
 * Determines if this [APIError] represents a client error.
 *
 * Client errors are HTTP status codes in the 400-499 range, indicating that the request contains
 * bad syntax or cannot be fulfilled by the server due to client-side issues.
 *
 * @return `true` if the error code is in the 400-499 range, `false` otherwise.
 */
public val APIError.isClientError: Boolean
    get() = statusCode in 400..499
