package io.getstream.feeds.android.client.internal.socket.common.parser

/**
 * Generic parser interface for encoding and decoding objects.
 */
internal interface GenericParser<E, E2> {
    /** Encodes the given object into a ByteString. */
    fun encode(event: E): Result<String>

    /** Decodes the given [raw] [ByteArray] into an object of type [E2]. */
    fun decode(raw: String): Result<E2>
}
