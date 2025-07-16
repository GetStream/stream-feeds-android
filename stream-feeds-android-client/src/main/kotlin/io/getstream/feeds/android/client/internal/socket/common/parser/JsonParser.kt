package io.getstream.feeds.android.client.internal.socket.common.parser

import com.squareup.moshi.Moshi

/**
 * A general Json parser.
 */
internal interface JsonParser {

    /**
     * Converts an object to its JSON representation.
     *
     * @param any The object to convert.
     * @return The JSON string representation of the object.
     */
    fun toJson(any: Any): String

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param raw The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return The object of type [T] parsed from the JSON string.
     */
    fun <T : Any> fromJson(raw: String, clazz: Class<T>): T

    /**
     * Converts a JSON string to an object of the specified class, returning a [Result].
     *
     * @param raw The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return A [Result] containing the parsed object or an error if parsing fails.
     */
    fun <T : Any> fromJsonOrError(raw: String, clazz: Class<T>): Result<T> {
        return try {
            val result = fromJson(raw, clazz)
            Result.success(result)
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }
}

/**
 * A JSON parser implementation using Moshi.
 *
 * @param moshi The Moshi instance to use for parsing.
 */
internal class MoshiJsonParser(private val moshi: Moshi) : JsonParser {

    override fun toJson(any: Any): String {
        return moshi.adapter(any.javaClass).toJson(any)
    }

    override fun <T : Any> fromJson(raw: String, clazz: Class<T>): T {
        return moshi.adapter(clazz).fromJson(raw)
            ?: throw IllegalArgumentException("Failed to parse $clazz from raw string: $raw")
    }
}
