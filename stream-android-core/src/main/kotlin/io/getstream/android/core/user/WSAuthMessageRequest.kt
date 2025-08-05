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
 * @property token The authentication token for the user.
 * @property userDetails The details of the user to be connected.
 */
@JsonClass(generateAdapter = true)
public data class WSAuthMessageRequest(
    @Json(name = "products") val products: List<String>,
    @Json(name = "token") val token: String,
    @Json(name = "user_details") val userDetails: ConnectUserDetailsRequest,
)
