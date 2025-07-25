package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.ActivityCommentListStateUpdates
import io.getstream.feeds.android.core.generated.models.CommentAddedEvent
import io.getstream.feeds.android.core.generated.models.CommentDeletedEvent
import io.getstream.feeds.android.core.generated.models.CommentReactionAddedEvent
import io.getstream.feeds.android.core.generated.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.core.generated.models.CommentUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class ActivityCommentListEventHandler(
    private val objectId: String,
    private val objectType: String,
    private val state: ActivityCommentListStateUpdates,
) : StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is CommentAddedEvent -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentAdded(ThreadedCommentData(event.comment.toModel()))
                }
            }

            is CommentDeletedEvent -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentRemoved(event.comment.id)
                }
            }

            is CommentUpdatedEvent -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentUpdated(event.comment.toModel())
                }
            }

            is CommentReactionAddedEvent -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentReactionAdded(event.comment.id, event.reaction.toModel())
                }
            }

            is CommentReactionDeletedEvent -> {
                if (event.comment.objectId == objectId && event.comment.objectType == objectType) {
                    state.onCommentReactionRemoved(event.comment.id, event.reaction.toModel())
                }
            }
        }
    }
}