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
package io.getstream.android.core.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents a request to connect a user to the Stream service.
 *
 * Note: While this class can be generated from the OpenAPI specification, it is defined here to
 * allow usage across the different Stream products without the need to depend on OpenAPI codegen.
 *
 * @property products The list of products the user is connecting to. Available products:
 * - `chat`
 * - `video`
 * - `feeds`
 *
 * @property token The authentication token for the user.
 * @property userDetails The details of the user to be connected.
 */
@JsonClass(generateAdapter = true)
public data class WSAuthMessageRequest(
    @Json(name = "products") val products: List<String>,
    @Json(name = "token") val token: String,
    @Json(name = "user_details") val userDetails: ConnectUserDetailsRequest,
)
