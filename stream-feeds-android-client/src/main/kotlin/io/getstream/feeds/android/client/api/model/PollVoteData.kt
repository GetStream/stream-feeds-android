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
package io.getstream.feeds.android.client.api.model

import java.util.Date

/**
 * Data class representing a poll vote.
 *
 * @property answerText The text of the answer, if any.
 * @property createdAt The date and time when the vote was created.
 * @property id Unique identifier for the poll vote.
 * @property isAnswer Whether this vote is the answer, if known.
 * @property optionId The ID of the selected option.
 * @property pollId The ID of the poll.
 * @property updatedAt The date and time when the vote was last updated.
 * @property user The user who voted, if available.
 * @property userId The ID of the user who voted, if available.
 */
public data class PollVoteData(
    val answerText: String?,
    val createdAt: Date,
    val id: String,
    val isAnswer: Boolean?,
    val optionId: String,
    val pollId: String,
    val updatedAt: Date,
    val user: UserData?,
    val userId: String?,
)
