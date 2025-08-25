package io.getstream.feeds.android.client.internal.serialization

import com.squareup.moshi.Moshi
import io.getstream.android.core.api.serialization.StreamJsonSerialization

/**
 * A JSON parser implementation using Moshi.
 *
 * @param moshi The Moshi instance to use for parsing.
 */
internal class FeedsMoshiJsonParser(private val moshi: Moshi) : StreamJsonSerialization {

    override fun toJson(any: Any): Result<String> = runCatching {
        moshi.adapter(any.javaClass).toJson(any)
    }

    override fun <T : Any> fromJson(raw: String, clazz: Class<T>): Result<T> = runCatching {
        moshi.adapter(clazz).fromJson(raw)
            ?: throw IllegalArgumentException("Failed to parse $clazz from raw string: $raw")
    }
}