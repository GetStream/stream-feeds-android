package io.getstream.feeds.android.client.internal.log

import io.getstream.log.StreamLog
import io.getstream.log.TaggedLogger

/**
 * Provides a logger for the given tag.
 *
 * @param prefix The prefix to be used for the tag.
 * @param tag The tag to be used for the logger.
 */
internal fun provideLogger(prefix: String = "Feeds", tag: String): TaggedLogger =
    StreamLog.getLogger("$prefix:$tag")
