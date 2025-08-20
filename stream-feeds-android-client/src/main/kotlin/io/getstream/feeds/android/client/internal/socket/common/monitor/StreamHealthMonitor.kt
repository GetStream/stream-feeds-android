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
package io.getstream.feeds.android.client.internal.socket.common.monitor

import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.log.TaggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Monitors the health of a socket connection by periodically checking for liveness.
 *
 * @param logger Logger for logging health monitor events.
 * @param scope Coroutine scope for launching the health monitor job.
 * @param interval Time in milliseconds between health checks. Defaults to 25 seconds.
 * @param livenessThreshold Time in milliseconds after which the connection is considered dead if no
 *   acknowledgment is received. Defaults to 60 seconds.
 */
internal class StreamHealthMonitor(
    private val logger: TaggedLogger = provideLogger(tag = "HealthMonitor"),
    private val scope: CoroutineScope,
    private val interval: Long = INTERVAL,
    private val livenessThreshold: Long = ALIVE_THRESHOLD,
) {

    companion object {
        const val INTERVAL = 25_000L
        const val ALIVE_THRESHOLD = 60_000L
    }

    private var monitorJob: Job? = null
    private var lastAck: Long = Clock.System.now().toEpochMilliseconds()

    // callbacks default to no-op
    private var onIntervalCallback: () -> Unit = {}
    private var onLivenessThresholdCallback: () -> Unit = {}

    /** Register a callback to run every [interval] ms *if* the socket is still considered alive. */
    fun onInterval(callback: () -> Unit) {
        onIntervalCallback = callback
    }

    /**
     * Register a callback to run when no ack has been received for more than [livenessThreshold]
     * ms.
     */
    fun onLivenessThreshold(callback: () -> Unit) {
        onLivenessThresholdCallback = callback
    }

    /** Call this whenever you get a “pong” or other liveness signal over the socket */
    fun ack() {
        lastAck = Clock.System.now().toEpochMilliseconds()
    }

    /** Starts (or restarts) the periodic health-check loop */
    fun start() {
        logger.d { "[start] Staring health monitor" }
        if (monitorJob?.isActive == true) {
            logger.d { "Health monitor already running" }
            return
        }
        monitorJob =
            scope.launch {
                while (isActive) {
                    delay(interval)

                    val now = Clock.System.now().toEpochMilliseconds()
                    if (now - lastAck > livenessThreshold) {
                        logger.d { "Liveness threshold reached" }
                        onLivenessThresholdCallback()
                    } else {
                        logger.d { "Running health check" }
                        onIntervalCallback()
                    }
                }
            }
    }

    /** Stops the health-check loop */
    fun stop() {
        logger.d { "[stop] Stopping heath monitor" }
        monitorJob?.cancel()
    }
}
