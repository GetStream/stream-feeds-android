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
package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of an activity.
 *
 * This class manages the state of a single activity including its comments, poll data, and
 * real-time updates. It automatically updates when WebSocket events are received and provides
 * change handlers for state modifications.
 */
public interface ActivityState {

    /** The current activity data. */
    public val activity: StateFlow<ActivityData?>

    /** The list of comments for this activity, sorted by default sorting criteria. */
    public val comments: StateFlow<List<ThreadedCommentData>>

    /** The poll data associated with this activity, if any. */
    public val poll: StateFlow<PollData?>
}
