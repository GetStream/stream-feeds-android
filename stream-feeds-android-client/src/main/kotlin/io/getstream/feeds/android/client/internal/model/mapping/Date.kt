package io.getstream.feeds.android.client.internal.model.mapping

import org.threeten.bp.OffsetDateTime
import java.util.Date

/**
 * Converts an [OffsetDateTime] to a [Date].
 *
 * @return A [Date] representing the same point in time as the [OffsetDateTime].
 * TODO: !!!Panic!!!
 *  This conversion loses precision from nanoseconds to milliseconds.
 *  Check if this is acceptable!
 * TODO: !!!Panic!!!
 */
internal fun OffsetDateTime.toDate(): Date = Date(this.toInstant().toEpochMilli())