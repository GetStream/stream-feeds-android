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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.concurrent.thread

internal class DefaultRetryStrategyTest {

    private val strategy = DefaultRetryStrategy()

    @Test
    fun `on initial state, then consecutiveFailures is zero`() {
        assertEquals(0, strategy.consecutiveFailures)
    }

    @Test
    fun `on first retry, then delay is zero`() {
        val delay = strategy.nextRetryDelay()

        assertEquals(0L, delay)
    }

    @Test
    fun `on incrementConsecutiveFailures, then count increases`() {
        strategy.incrementConsecutiveFailures()

        assertEquals(1, strategy.consecutiveFailures)

        strategy.incrementConsecutiveFailures()

        assertEquals(2, strategy.consecutiveFailures)
    }

    @Test
    fun `on resetConsecutiveFailures, then count becomes zero`() {
        strategy.incrementConsecutiveFailures()
        strategy.incrementConsecutiveFailures()

        strategy.resetConsecutiveFailures()

        assertEquals(0, strategy.consecutiveFailures)
    }

    @Test
    fun `on resetConsecutiveFailures, then next delay is zero`() {
        strategy.incrementConsecutiveFailures()
        strategy.resetConsecutiveFailures()

        val delay = strategy.nextRetryDelay()

        assertEquals(0L, delay)
    }

    @Test
    fun `on one failure, then delay is within expected range`() {
        strategy.incrementConsecutiveFailures()

        val delay = strategy.nextRetryDelay()

        assertTrue("Delay $delay should be between 250 and 2500", delay in 250..2500)
    }

    @Test
    fun `on two failures, then delay is within expected range`() {
        strategy.incrementConsecutiveFailures()
        strategy.incrementConsecutiveFailures()

        val delay = strategy.nextRetryDelay()

        assertTrue("Delay $delay should be between 2000 and 4500", delay in 2000..4500)
    }

    @Test
    fun `on three failures, then delay is within expected range`() {
        strategy.incrementConsecutiveFailures()
        strategy.incrementConsecutiveFailures()
        strategy.incrementConsecutiveFailures()

        val delay = strategy.nextRetryDelay()

        assertTrue("Delay $delay should be between 4000 and 6500", delay in 4000..6500)
    }

    @Test
    fun `on many failures, then delay is capped at maximum`() {
        repeat(20) { strategy.incrementConsecutiveFailures() }

        val delay = strategy.nextRetryDelay()

        assertTrue("Delay $delay should be at most 25000", delay <= 25000)
    }

    @Test
    fun `on many failures, then delay is at least minimum for max range`() {
        repeat(20) { strategy.incrementConsecutiveFailures() }

        val delay = strategy.nextRetryDelay()

        assertEquals(25000L, delay)
    }

    @Test
    fun `on multiple calls with same failures, then delays vary due to jitter`() {
        strategy.incrementConsecutiveFailures()
        strategy.incrementConsecutiveFailures()

        val delays = (1..100).map { strategy.nextRetryDelay() }
        val uniqueDelays = delays.toSet()

        assertTrue("Should have multiple different delays due to jitter, got: $uniqueDelays",
                   uniqueDelays.size > 1)
    }

    @Test
    fun `on edge case calculation, then verify specific failure counts`() {
        // Test the edge case where minDelay calculation might be tricky
        repeat(12) { strategy.incrementConsecutiveFailures() }

        val delay = strategy.nextRetryDelay()

        // With 12 failures:
        // maxDelay = min(500 + 12*2000, 25000) = min(24500, 25000) = 24500
        // minDelay = min(max(250, 11*2000), 25000) = min(22000, 25000) = 22000
        assertTrue("Delay $delay should be between 22000 and 24500", delay in 22000..24500)
    }

    @Test
    fun `on exactly max calculation threshold, then verify boundary`() {
        repeat(13) { strategy.incrementConsecutiveFailures() }

        val delay = strategy.nextRetryDelay()

        // With 13 failures:
        // maxDelay = min(500 + 13*2000, 25000) = min(26500, 25000) = 25000
        // minDelay = min(max(250, 12*2000), 25000) = min(24000, 25000) = 24000
        assertTrue("Delay $delay should be between 24000 and 25000", delay in 24000..25000)
    }

    @Test
    fun `on thread safety, then concurrent access works correctly`() {
        val threads = (1..10).map {
            thread {
                repeat(5) {
                    strategy.incrementConsecutiveFailures()
                    strategy.nextRetryDelay()
                }
            }
        }

        threads.forEach { it.join() }

        // Should have incremented 10 threads * 5 times = 50
        assertEquals(50, strategy.consecutiveFailures)
    }
}
