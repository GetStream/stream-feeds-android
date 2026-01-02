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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.FeedInputData
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.network.models.FeedInput

/**
 * Converts a [io.getstream.feeds.android.network.models.FeedInput] to a
 * [io.getstream.feeds.android.client.api.model.FeedInputData] model.
 */
internal fun FeedInput.toModel(): FeedInputData =
    FeedInputData(
        description = description,
        name = name,
        visibility = visibility?.toModel(),
        filterTags = filterTags.orEmpty(),
        members = members?.map { it.toModel() }.orEmpty(),
        custom = custom.orEmpty(),
    )

/**
 * Converts a [io.getstream.feeds.android.network.models.FeedInput.Visibility] to a
 * [io.getstream.feeds.android.client.api.model.FeedVisibility] model.
 */
internal fun FeedInput.Visibility.toModel(): FeedVisibility =
    when (this) {
        FeedInput.Visibility.Followers -> FeedVisibility.Followers
        FeedInput.Visibility.Members -> FeedVisibility.Members
        FeedInput.Visibility.Private -> FeedVisibility.Private
        FeedInput.Visibility.Public -> FeedVisibility.Public
        FeedInput.Visibility.Visible -> FeedVisibility.Visible
        is FeedInput.Visibility.Unknown -> FeedVisibility.Unknown(unknownValue)
    }

/**
 * Converts a [io.getstream.feeds.android.client.api.model.FeedInputData] to a
 * [io.getstream.feeds.android.network.models.FeedInput] model.
 */
internal fun FeedInputData.toRequest(): FeedInput =
    FeedInput(
        description = description,
        name = name,
        visibility = visibility?.toRequest(),
        filterTags = filterTags.takeIf { it.isNotEmpty() },
        members = members.takeIf { it.isNotEmpty() }?.map { it.toRequest() },
        custom = custom.takeIf { it.isNotEmpty() },
    )

/**
 * Converts a [io.getstream.feeds.android.client.api.model.FeedVisibility] to a
 * [io.getstream.feeds.android.network.models.FeedInput.Visibility] model.
 */
internal fun FeedVisibility.toRequest(): FeedInput.Visibility =
    when (this) {
        FeedVisibility.Followers -> FeedInput.Visibility.Followers
        FeedVisibility.Members -> FeedInput.Visibility.Members
        FeedVisibility.Private -> FeedInput.Visibility.Private
        FeedVisibility.Public -> FeedInput.Visibility.Public
        FeedVisibility.Visible -> FeedInput.Visibility.Visible
        is FeedVisibility.Unknown -> FeedInput.Visibility.Unknown(unknownValue)
    }
