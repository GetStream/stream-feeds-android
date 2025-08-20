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

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.CommentListStateUpdates
import io.getstream.feeds.android.core.generated.models.CommentUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class CommentListEventHandler(private val state: CommentListStateUpdates) :
    StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is CommentUpdatedEvent -> state.onCommentUpdated(event.comment.toModel())
        }
    }
}
