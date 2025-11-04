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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.android.core.api.filter.toRequest
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.model.toRequest
import io.getstream.feeds.android.network.models.GetOrCreateFeedRequest
import io.getstream.feeds.android.network.models.PagerRequest

/**
 * Converts this [io.getstream.feeds.android.client.api.state.query.FeedQuery] to a
 * [io.getstream.feeds.android.network.models.GetOrCreateFeedRequest].
 *
 * @return A [GetOrCreateFeedRequest] representing the feed query.
 */
internal fun FeedQuery.toRequest(): GetOrCreateFeedRequest =
    GetOrCreateFeedRequest(
        limit = activityLimit,
        next = activityNext,
        prev = activityPrevious,
        view = view,
        watch = watch,
        activitySelectorOptions = activitySelectorOptions,
        data = data?.toRequest(),
        externalRanking = externalRanking,
        filter = activityFilter?.toRequest(),
        followersPagination = followerLimit?.let { PagerRequest(it) },
        followingPagination = followingLimit?.let { PagerRequest(it) },
        interestWeights = interestWeights,
        memberPagination = memberLimit?.let { PagerRequest(it) },
    )
