package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.core.generated.models.ActivityDeletedEvent
import io.getstream.feeds.android.core.generated.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.core.generated.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkAddedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkDeletedEvent
import io.getstream.feeds.android.core.generated.models.CommentAddedEvent
import io.getstream.feeds.android.core.generated.models.CommentDeletedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class ActivityListEventHandler(
    private val state: ActivityListStateUpdates
): StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is ActivityDeletedEvent -> state.onActivityRemoved(event.activity.toModel())
            is ActivityReactionAddedEvent -> state.onReactionAdded(event.reaction.toModel())
            is ActivityReactionDeletedEvent -> state.onReactionRemoved(event.reaction.toModel())
            is BookmarkAddedEvent -> state.onBookmarkAdded(event.bookmark.toModel())
            is BookmarkDeletedEvent -> state.onBookmarkRemoved(event.bookmark.toModel())
            is CommentAddedEvent -> state.onCommentAdded(event.comment.toModel())
            is CommentDeletedEvent -> state.onCommentRemoved(event.comment.toModel())
            else -> {
                // No action needed for other event types
            }
        }
    }
}