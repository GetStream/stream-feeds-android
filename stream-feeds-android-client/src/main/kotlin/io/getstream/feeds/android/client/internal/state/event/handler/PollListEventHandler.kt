package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.PollListStateUpdates
import io.getstream.feeds.android.core.generated.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * Handles events related to poll updates in the poll list state.
 *
 * @property state The instance that manages updates to the poll list state.
 */
internal class PollListEventHandler(
    private val state: PollListStateUpdates,
): StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is PollUpdatedFeedEvent -> {
                state.onPollUpdated(event.poll.toModel())
            }
        }
    }
}