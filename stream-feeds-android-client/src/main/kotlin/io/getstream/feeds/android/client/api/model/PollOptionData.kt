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

import io.getstream.feeds.android.core.generated.models.PollOptionResponseData

/**
 * Data class representing a poll option.
 *
 * @property custom Custom data as a map.
 * @property id Unique identifier for the poll option.
 * @property text The text of the poll option.
 */
public data class PollOptionData(val custom: Map<String, Any?>, val id: String, val text: String)

/** Converts a [PollOptionResponseData] to a [PollOptionData] model. */
internal fun PollOptionResponseData.toModel(): PollOptionData =
    PollOptionData(custom = custom, id = id, text = text)
