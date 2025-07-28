/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class ExternalStorage (
    @Json(name = "abs_account_name")
    val absAccountName: kotlin.String? = null,

    @Json(name = "abs_client_id")
    val absClientId: kotlin.String? = null,

    @Json(name = "abs_client_secret")
    val absClientSecret: kotlin.String? = null,

    @Json(name = "abs_tenant_id")
    val absTenantId: kotlin.String? = null,

    @Json(name = "bucket")
    val bucket: kotlin.String? = null,

    @Json(name = "gcs_credentials")
    val gcsCredentials: kotlin.String? = null,

    @Json(name = "path")
    val path: kotlin.String? = null,

    @Json(name = "s3_api_key")
    val s3ApiKey: kotlin.String? = null,

    @Json(name = "s3_custom_endpoint")
    val s3CustomEndpoint: kotlin.String? = null,

    @Json(name = "s3_region")
    val s3Region: kotlin.String? = null,

    @Json(name = "s3_secret_key")
    val s3SecretKey: kotlin.String? = null,

    @Json(name = "storage_name")
    val storageName: kotlin.String? = null,

    @Json(name = "storage_type")
    val storageType: kotlin.Int? = null
)
