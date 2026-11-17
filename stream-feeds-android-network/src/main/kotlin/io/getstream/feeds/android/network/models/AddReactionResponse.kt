/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class AddReactionResponse(
    @Json(name = "counter_delta") public val counterDelta: Int,
    @Json(name = "duration") public val duration: String,
    @Json(name = "outcome") public val outcome: String,
    @Json(name = "activity")
    public val activity: io.getstream.feeds.android.network.models.ActivityResponse,
    @Json(name = "reaction")
    public val reaction: io.getstream.feeds.android.network.models.FeedsReactionResponse,
    @Json(name = "notification_accepted") public val notificationAccepted: Boolean? = null,
    @Json(name = "notification_created") public val notificationCreated: Boolean? = null,
    @Json(name = "notification_task_id") public val notificationTaskId: String? = null,
    @Json(name = "previous_reaction_type") public val previousReactionType: String? = null,
    @Json(name = "reference_activity")
    public val referenceActivity: io.getstream.feeds.android.network.models.ActivityResponse? = null,
)
