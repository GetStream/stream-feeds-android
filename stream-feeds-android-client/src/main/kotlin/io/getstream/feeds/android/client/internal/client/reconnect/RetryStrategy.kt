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

package io.getstream.feeds.android.client.internal.client.reconnect

import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Interface that encapsulates the logic for computing delays for failed actions that need to be
 * retried.
 *
 * This strategy manages the retry delay calculation with exponential backoff and jitter to avoid
 * overwhelming the backend service with simultaneous retry attempts from multiple clients.
 */
internal interface RetryStrategy {

    /**
     * The number of consecutively failed retries.
     *
     * @return The current count of consecutive failures
     */
    val consecutiveFailures: Int

    /**
     * Increments the number of consecutively failed retries, making the next delay longer.
     *
     * This method should be called each time a retry attempt fails to gradually increase the delay
     * between subsequent attempts.
     */
    fun incrementConsecutiveFailures()

    /**
     * Resets the number of consecutively failed retries, making the next delay be the shortest one.
     *
     * This method should be called when a retry attempt succeeds to reset the backoff strategy for
     * future retry attempts.
     */
    fun resetConsecutiveFailures()

    /**
     * Calculates and returns the delay for the next retry attempt.
     *
     * Consecutive calls after the same number of failures may return different delays due to
     * randomization (jitter). This randomization helps avoid overwhelming the backend by preventing
     * all clients from retrying at exactly the same time.
     *
     * @return The delay for the next retry in milliseconds
     */
    fun nextRetryDelay(): Long
}

/**
 * The default implementation of [RetryStrategy] with exponentially growing delays and jitter.
 *
 * This implementation uses an exponential backoff strategy with randomization to distribute retry
 * attempts over time. The delay increases with each consecutive failure up to a maximum limit to
 * balance between quick recovery and avoiding server overload.
 */
internal class DefaultRetryStrategy : RetryStrategy {

    companion object {
        /**
         * Maximum delay between reconnection attempts in milliseconds (25 seconds).
         *
         * This prevents the retry delay from growing indefinitely and ensures that reconnection
         * attempts don't become excessively infrequent.
         */
        private const val MAXIMUM_RECONNECTION_DELAY_MS = 25_000L // 25 seconds
    }

    /** Thread-safe counter for tracking consecutive failures. */
    private val consecutiveFailuresCount = AtomicInteger(0)

    /**
     * Returns the current number of consecutive failures.
     *
     * @return The number of consecutive failed retry attempts
     */
    override val consecutiveFailures: Int
        get() = consecutiveFailuresCount.get()

    /**
     * Atomically increments the consecutive failures counter.
     *
     * This increases the delay for subsequent retry attempts according to the exponential backoff
     * algorithm.
     */
    override fun incrementConsecutiveFailures() {
        consecutiveFailuresCount.incrementAndGet()
    }

    /**
     * Atomically resets the consecutive failures counter to zero.
     *
     * This should be called when a connection succeeds to reset the retry delay for future
     * attempts.
     */
    override fun resetConsecutiveFailures() {
        consecutiveFailuresCount.set(0)
    }

    /**
     * Calculates the next retry delay using exponential backoff with jitter.
     *
     * The algorithm works as follows:
     * - First retry: No delay (0ms)
     * - Subsequent retries: Random delay between calculated min and max bounds
     * - Delay bounds increase exponentially with each failure
     * - Maximum delay is capped at [MAXIMUM_RECONNECTION_DELAY_MS]
     *
     * @return The delay for the next retry in milliseconds, or 0 for the first retry
     */
    override fun nextRetryDelay(): Long {
        val currentFailures = consecutiveFailures

        // The first time we get to retry, we do it without any delay. Any subsequent time will
        // be delayed by a random interval.
        if (currentFailures <= 0) {
            return 0L
        }

        val maxDelay = min(500L + (currentFailures * 2000L), MAXIMUM_RECONNECTION_DELAY_MS)
        val minDelay = min(max(250L, (currentFailures - 1) * 2000L), MAXIMUM_RECONNECTION_DELAY_MS)

        return Random.nextLong(minDelay, maxDelay + 1)
    }
}
