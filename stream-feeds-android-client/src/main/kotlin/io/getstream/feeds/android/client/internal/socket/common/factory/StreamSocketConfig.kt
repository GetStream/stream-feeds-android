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
package io.getstream.feeds.android.client.internal.socket.common.factory

import io.getstream.android.core.http.XStreamClient
import io.getstream.android.core.user.ApiKey

/**
 * Internal configuration for the Stream socket.
 *
 * @param url The URL to connect to.
 * @param apiKey The API key for authentication.
 * @param authType The type of authentication used (e.g., "jwt").
 * @param xStreamClient The client identifier for the Stream service.
 */
internal class StreamSocketConfig(
    val url: String,
    val apiKey: ApiKey,
    val authType: String,
    val xStreamClient: XStreamClient,
)
