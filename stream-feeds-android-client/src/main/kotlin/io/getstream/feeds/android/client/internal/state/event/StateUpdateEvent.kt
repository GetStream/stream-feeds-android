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
package io.getstream.feeds.android.client.internal.state.event

import io.getstream.feeds.android.network.models.WSEvent

/**
 * Represents an event that may trigger a state update. These events are typically the result of
 * receiving a WebSocket event or having executed a successful API call that can modify the state.
 */
internal sealed interface StateUpdateEvent {}

internal fun WSEvent.toModel(): StateUpdateEvent? =
    when (this) {
        is CommentAddedEvent -> StateUpdateEvent.CommentAdded(comment.toModel())

        is CommentUpdatedEvent -> StateUpdateEvent.CommentUpdated(comment.toModel())

        is CommentDeletedEvent -> StateUpdateEvent.CommentDeleted(comment.toModel())

        is CommentReactionAddedEvent ->
            StateUpdateEvent.CommentReactionAdded(comment.toModel(), reaction.toModel())

        is CommentReactionDeletedEvent ->
            StateUpdateEvent.CommentReactionDeleted(comment.toModel(), reaction.toModel())

        else -> null
    }
}
