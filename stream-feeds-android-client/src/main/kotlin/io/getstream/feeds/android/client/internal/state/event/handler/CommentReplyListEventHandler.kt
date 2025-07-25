package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.CommentReplyListStateUpdates
import io.getstream.feeds.android.core.generated.models.CommentAddedEvent
import io.getstream.feeds.android.core.generated.models.CommentDeletedEvent
import io.getstream.feeds.android.core.generated.models.CommentReactionAddedEvent
import io.getstream.feeds.android.core.generated.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.core.generated.models.CommentUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class CommentReplyListEventHandler(
    private val state: CommentReplyListStateUpdates,
) : StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is CommentAddedEvent -> {
                state.onCommentAdded(ThreadedCommentData(event.comment.toModel()))
            }
            is CommentDeletedEvent -> {
                state.onCommentRemoved(event.comment.id)
            }
            is CommentUpdatedEvent -> {
                state.onCommentUpdated(event.comment.toModel())
            }
            is CommentReactionAddedEvent -> {
                state.onCommentReactionAdded(event.comment.id, event.reaction.toModel())
            }
            is CommentReactionDeletedEvent -> {
                state.onCommentReactionRemoved(event.comment.id, event.reaction.toModel())
            }
        }
    }
}