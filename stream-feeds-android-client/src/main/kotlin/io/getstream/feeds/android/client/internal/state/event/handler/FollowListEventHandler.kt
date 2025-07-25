package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.FollowListStateUpdates
import io.getstream.feeds.android.core.generated.models.FollowUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class FollowListEventHandler(
    private val state: FollowListStateUpdates,
) : StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is FollowUpdatedEvent -> {
                state.onFollowUpdated(event.follow.toModel())
            }
        }
    }
}