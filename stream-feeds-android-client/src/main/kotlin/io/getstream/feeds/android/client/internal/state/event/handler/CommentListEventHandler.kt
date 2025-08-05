package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.CommentListStateUpdates
import io.getstream.feeds.android.core.generated.models.CommentUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class CommentListEventHandler(
    private val state: CommentListStateUpdates,
) : StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is CommentUpdatedEvent -> state.onCommentUpdated(event.comment.toModel())
        }
    }
}