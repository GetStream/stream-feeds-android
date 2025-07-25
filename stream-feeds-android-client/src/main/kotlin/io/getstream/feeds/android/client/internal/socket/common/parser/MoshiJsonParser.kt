package io.getstream.feeds.android.client.internal.socket.common.parser

import com.squareup.moshi.Moshi
import io.getstream.android.core.parser.JsonParser

/**
 * A JSON parser implementation using Moshi.
 *
 * @param moshi The Moshi instance to use for parsing.
 */
public class MoshiJsonParser(private val moshi: Moshi) : JsonParser {

    override fun toJson(any: Any): String {
        return moshi.adapter(any.javaClass).toJson(any)
    }

    override fun <T : Any> fromJson(raw: String, clazz: Class<T>): T {
        return moshi.adapter(clazz).fromJson(raw)
            ?: throw IllegalArgumentException("Failed to parse $clazz from raw string: $raw")
    }
}
