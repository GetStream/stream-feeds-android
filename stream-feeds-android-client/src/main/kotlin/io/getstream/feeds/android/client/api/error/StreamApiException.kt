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
public class StreamApiException(public val apiError: APIError) : IOException()
