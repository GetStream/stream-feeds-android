package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * An interface for handling state events in the Stream Feeds Android client.
 * Implementations of this interface should define how to process different types of events.
 */
internal interface StateEventHandler {

    /**
     * Handles a given [WSEvent].
     *
     */
    fun handleEvent(event: WSEvent)
}