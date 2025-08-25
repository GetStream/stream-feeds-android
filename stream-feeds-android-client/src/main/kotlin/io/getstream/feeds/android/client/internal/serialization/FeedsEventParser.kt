package io.getstream.feeds.android.client.internal.serialization

import io.getstream.android.core.api.serialization.StreamJsonSerialization
import io.getstream.android.core.api.serialization.StreamProductEventSerialization
import io.getstream.feeds.android.network.models.WSEvent

/**
 * Parser for WebSocket events in the Feeds client.
 *
 * @property jsonParser The JSON parser used to serialize and deserialize events.
 */
internal class FeedsEventParser(private val jsonParser: StreamJsonSerialization) :
    StreamProductEventSerialization<WSEvent> {

    override fun serialize(data: WSEvent): Result<String> = jsonParser.toJson(data)

    override fun deserialize(raw: String): Result<WSEvent> = jsonParser.fromJson(raw, WSEvent::class.java)
}