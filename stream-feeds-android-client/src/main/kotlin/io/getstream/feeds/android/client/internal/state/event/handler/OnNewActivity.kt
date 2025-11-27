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

package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.state.InsertionAction
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.state.query.matches

internal typealias OnNewActivity =
    (FeedQuery, ActivityData, currentUserId: String) -> InsertionAction

internal fun defaultOnNewActivity(
    query: FeedQuery,
    activity: ActivityData,
    currentUserId: String,
): InsertionAction =
    if (activity.user.id == currentUserId && activity matches query.activityFilter) {
        InsertionAction.AddToStart
    } else {
        InsertionAction.Ignore
    }
