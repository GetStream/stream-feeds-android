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
@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.io.*

/**  */
public data class ExternalStorage(
    @Json(name = "abs_account_name") public val absAccountName: kotlin.String? = null,
    @Json(name = "abs_client_id") public val absClientId: kotlin.String? = null,
    @Json(name = "abs_client_secret") public val absClientSecret: kotlin.String? = null,
    @Json(name = "abs_tenant_id") public val absTenantId: kotlin.String? = null,
    @Json(name = "bucket") public val bucket: kotlin.String? = null,
    @Json(name = "gcs_credentials") public val gcsCredentials: kotlin.String? = null,
    @Json(name = "path") public val path: kotlin.String? = null,
    @Json(name = "s3_api_key") public val s3ApiKey: kotlin.String? = null,
    @Json(name = "s3_custom_endpoint") public val s3CustomEndpoint: kotlin.String? = null,
    @Json(name = "s3_region") public val s3Region: kotlin.String? = null,
    @Json(name = "s3_secret_key") public val s3SecretKey: kotlin.String? = null,
    @Json(name = "storage_name") public val storageName: kotlin.String? = null,
    @Json(name = "storage_type") public val storageType: kotlin.Int? = null,
)
