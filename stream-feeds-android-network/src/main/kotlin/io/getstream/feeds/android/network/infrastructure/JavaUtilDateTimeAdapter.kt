/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.feeds.android.network.infrastructure

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A Moshi JSON adapter for handling date serialization and deserialization between JSON and Java
 * [Date] objects.
 *
 * This adapter provides bidirectional conversion:
 * - **Serialization**: Converts [Date] objects to ISO 8601 formatted strings in UTC timezone
 * - **Deserialization**: Converts nanosecond precision epoch timestamps (as strings) to [Date]
 *   objects
 *
 * The adapter handles two different date formats depending on the direction of conversion:
 * - For outgoing JSON: Uses ISO 8601 format (`yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`)
 * - For incoming JSON: Expects nanosecond epoch timestamps as string values
 *
 * @see ToJson
 * @see FromJson
 */
public class JavaUtilDateTimeAdapter {

    /**
     * ISO 8601 date format pattern used for serializing dates to JSON. Format:
     * `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` (e.g., "2023-12-25T14:30:45.123Z")
     */
    private val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    /**
     * Pre-configured [SimpleDateFormat] instance for consistent date formatting.
     * - Uses US locale to ensure consistent formatting regardless of device locale
     * - Configured for UTC timezone to avoid timezone-related issues
     */
    private val dateFormat =
        SimpleDateFormat(DATE_FORMAT, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

    /**
     * Converts a [Date] object to its JSON string representation.
     *
     * Serializes the provided date using ISO 8601 format in UTC timezone. This method is
     * automatically called by Moshi when serializing objects that contain [Date] fields.
     *
     * @param value The [Date] object to serialize
     * @return ISO 8601 formatted date string in UTC (e.g., "2023-12-25T14:30:45.123Z")
     * @see ToJson
     */
    @ToJson
    public fun toJson(value: Date): String {
        return dateFormat.format(value)
    }

    /**
     * Converts a JSON string representation to a [Date] object.
     *
     * Deserializes date values from JSON, expecting nanosecond precision epoch timestamps as string
     * values. The method converts nanoseconds to milliseconds by dividing by 1,000,000 since Java
     * [Date] uses millisecond precision.
     *
     * This method is automatically called by Moshi when deserializing JSON that contains date
     * fields.
     *
     * @param value String representation of nanosecond epoch timestamp
     * @return [Date] object representing the parsed timestamp
     * @throws NumberFormatException if the input string cannot be parsed as a valid long value
     * @see FromJson
     */
    @FromJson
    public fun fromJson(value: String): Date {
        // Convert nano epoch time to milliseconds
        val epochMillis = value.toLong() / 1_000_000L
        return Date(epochMillis)
    }
}
