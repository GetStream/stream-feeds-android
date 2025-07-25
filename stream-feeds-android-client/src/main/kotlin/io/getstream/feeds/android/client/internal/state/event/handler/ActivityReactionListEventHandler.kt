package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.ActivityReactionListStateUpdates
import io.getstream.feeds.android.core.generated.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class ActivityReactionListEventHandler(
    private val activityId: String,
    private val state: ActivityReactionListStateUpdates,
): StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is ActivityReactionDeletedEvent -> {
                if (event.activity.id == activityId) {
                    state.onReactionRemoved(event.reaction.toModel())
                }
            }
        }
    }
}