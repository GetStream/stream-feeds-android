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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.io.*

/**  */
data class PollVoteResponseData(
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "id") val id: kotlin.String,
    @Json(name = "option_id") val optionId: kotlin.String,
    @Json(name = "poll_id") val pollId: kotlin.String,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "answer_text") val answerText: kotlin.String? = null,
    @Json(name = "is_answer") val isAnswer: kotlin.Boolean? = null,
    @Json(name = "user_id") val userId: kotlin.String? = null,
    @Json(name = "user")
    val user: io.getstream.feeds.android.core.generated.models.UserResponse? = null,
)
