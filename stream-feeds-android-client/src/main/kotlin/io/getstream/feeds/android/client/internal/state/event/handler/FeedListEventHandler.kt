package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.FeedListStateUpdates
import io.getstream.feeds.android.core.generated.models.FeedUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class FeedListEventHandler(
    private val state: FeedListStateUpdates,
) : StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is FeedUpdatedEvent -> {
                state.onFeedUpdated(event.feed.toModel())
            }
        }
    }
}