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
public data class PollVote(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "option_id") public val optionId: kotlin.String,
    @Json(name = "poll_id") public val pollId: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "answer_text") public val answerText: kotlin.String? = null,
    @Json(name = "is_answer") public val isAnswer: kotlin.Boolean? = null,
    @Json(name = "user_id") public val userId: kotlin.String? = null,
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.User? = null,
)
