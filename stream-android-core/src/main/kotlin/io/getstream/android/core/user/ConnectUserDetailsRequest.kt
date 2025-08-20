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
 * Represents the details of a user to be connected to the Stream service.
 *
 * Note: While this class can be generated from the OpenAPI specification, it is defined here to
 * allow usage across the different Stream products without the need to depend on OpenAPI codegen.
 *
 * @property id The unique identifier for the user.
 * @property image The URL of the user's image (optional).
 * @property invisible Whether the user should be invisible (optional).
 * @property language The language preference for the user (optional).
 * @property name The name of the user (optional).
 * @property custom Custom data associated with the user, represented as a map (optional).
 */
@JsonClass(generateAdapter = true)
public data class ConnectUserDetailsRequest(
    @Json(name = "id") val id: String,
    @Json(name = "image") val image: String? = null,
    @Json(name = "invisible") val invisible: Boolean? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "custom") val custom: Map<String, Any?>? = null,
)
