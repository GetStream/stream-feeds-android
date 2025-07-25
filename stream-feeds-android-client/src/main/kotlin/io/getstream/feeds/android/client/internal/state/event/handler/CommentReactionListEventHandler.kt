package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.CommentReactionListStateUpdates
import io.getstream.feeds.android.core.generated.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class CommentReactionListEventHandler(
    private val state: CommentReactionListStateUpdates,
) : StateEventHandler {
    override fun handleEvent(event: WSEvent) {
        when (event) {
            is CommentReactionDeletedEvent -> state.onReactionRemoved(event.reaction.toModel())
        }
    }
}