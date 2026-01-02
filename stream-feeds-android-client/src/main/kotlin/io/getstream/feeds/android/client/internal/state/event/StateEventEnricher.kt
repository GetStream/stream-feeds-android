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

package io.getstream.feeds.android.client.internal.state.event

import io.getstream.feeds.android.client.internal.model.updateFeedOwnValues
import io.getstream.feeds.android.client.internal.repository.FeedOwnValuesRepository

internal class StateEventEnricher(private val feedOwnValuesRepository: FeedOwnValuesRepository) {
    fun enrich(event: StateUpdateEvent): StateUpdateEvent {
        return when (event) {
            is StateUpdateEvent.ActivityAdded -> enrich(event)
            else -> event
        }
    }

    private fun enrich(event: StateUpdateEvent.ActivityAdded): StateUpdateEvent.ActivityAdded {
        val feed = event.activity.currentFeed ?: return event
        val capabilities = feedOwnValuesRepository.getOrRequest(feed.fid) ?: return event

        return event.copy(activity = event.activity.updateFeedOwnValues(capabilities))
    }
}
