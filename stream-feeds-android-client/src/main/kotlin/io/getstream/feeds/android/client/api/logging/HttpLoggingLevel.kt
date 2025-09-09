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
package io.getstream.feeds.android.client.api.logging

/**
 * Represents the logging levels for HTTP requests and responses.
 * - None: No logging.
 * - Basic: Logs request and response lines.
 * - Headers: Logs request and response lines along with their respective headers.
 * - Body: Logs request and response lines, headers, and bodies (if present).
 */
public enum class HttpLoggingLevel {
    /** No logging. */
    None,

    /** Logs request and response lines. */
    Basic,

    /** Logs request and response lines along with their respective headers. */
    Headers,

    /** Logs request and response lines, headers, and bodies (if present). */
    Body,
}
