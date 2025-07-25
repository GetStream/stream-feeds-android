package io.getstream.android.core.parser

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi

/**
 * A general Json parser.
 */
@StreamInternalApi
public interface JsonParser {

    /**
     * Converts an object to its JSON representation.
     *
     * @param any The object to convert.
     * @return The JSON string representation of the object.
     */
    public fun toJson(any: Any): String

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param raw The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return The object of type [T] parsed from the JSON string.
     */
    public fun <T : Any> fromJson(raw: String, clazz: Class<T>): T

    /**
     * Converts a JSON string to an object of the specified class, returning a [Result].
     *
     * @param raw The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return A [Result] containing the parsed object or an error if parsing fails.
     */
    public fun <T : Any> fromJsonOrError(raw: String, clazz: Class<T>): Result<T> {
        return try {
            val result = fromJson(raw, clazz)
            Result.success(result)
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }
}
