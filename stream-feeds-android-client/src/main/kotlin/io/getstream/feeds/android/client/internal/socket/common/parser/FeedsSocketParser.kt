package io.getstream.feeds.android.client.internal.socket.common.parser

import io.getstream.android.core.parser.JsonParser
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.internal.socket.events.ConnectedEvent
import io.getstream.feeds.android.client.internal.socket.events.ConnectionErrorEvent
import io.getstream.feeds.android.client.internal.socket.events.EVENT_TYPE_CONNECTION_ERROR
import io.getstream.feeds.android.client.internal.socket.events.EVENT_TYPE_CONNECTION_OK
import io.getstream.feeds.android.core.generated.models.UnsupportedWSEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * Parser for WebSocket events in the Feeds client.
 *
 * @property jsonParser The JSON parser used to serialize and deserialize events.
 */
internal class FeedsEventParser(private val jsonParser: JsonParser) : GenericParser<Any, WSEvent> {

    override fun encode(event: Any): Result<String> = runSafely {
        jsonParser.toJson(event)
    }

    override fun decode(raw: String): Result<WSEvent> = runSafely {
        val event = jsonParser.fromJson(raw, WSEvent::class.java)
        if (event is UnsupportedWSEvent) {
            parseUnsupportedEvent(raw, event)
        } else {
            event
        } ?: throw IllegalArgumentException("Failed to parse WSEvent from raw string: $raw")
    }

    private fun parseUnsupportedEvent(raw: String, unsupported: UnsupportedWSEvent): WSEvent? {
        return when (unsupported.type) {
            EVENT_TYPE_CONNECTION_OK ->
                jsonParser.fromJson(raw, ConnectedEvent::class.java)

            EVENT_TYPE_CONNECTION_ERROR ->
                jsonParser.fromJson(raw, ConnectionErrorEvent::class.java)

            else -> unsupported
        }
    }
}
