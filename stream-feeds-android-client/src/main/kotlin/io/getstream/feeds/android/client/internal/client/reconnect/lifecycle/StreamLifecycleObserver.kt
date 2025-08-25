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
package io.getstream.feeds.android.client.internal.client.reconnect.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.getstream.android.core.annotations.StreamInternalApi
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A lifecycle observer that manages and notifies registered listeners about Android lifecycle
 * events.
 *
 * This observer automatically starts observing the lifecycle when the first listener is subscribed
 * and stops observing when all listeners are unsubscribed, providing efficient resource management.
 *
 * @param scope The [CoroutineScope] used to launch coroutines for notifying listeners
 * @param lifecycle The [Lifecycle] to observe for lifecycle events
 */
@StreamInternalApi
public class StreamLifecycleObserver(
    private val scope: CoroutineScope,
    private val lifecycle: Lifecycle,
) : DefaultLifecycleObserver {

    /**
     * Flag to track whether this is a recurring resume event. Used to ignore the first resume event
     * when initially observing the lifecycle.
     */
    private var recurringResumeEvent: Boolean = false

    /** Set of listeners that will be notified of lifecycle events. */
    private val listeners: MutableSet<LifecycleListener> = mutableSetOf<LifecycleListener>()

    /**
     * Atomic boolean to track whether the observer is currently active and observing the lifecycle.
     */
    private val isActive: AtomicBoolean = AtomicBoolean(false)

    /**
     * Called when the lifecycle owner resumes. Notifies all registered listeners of the resume
     * event, but ignores the first resume event that occurs immediately after starting to observe
     * the lifecycle.
     *
     * @param owner The lifecycle owner that resumed
     */
    override fun onResume(owner: LifecycleOwner) {
        // ignore event when we just started observing the lifecycle
        if (recurringResumeEvent) {
            scope.launch { listeners.forEach { it.onResume() } }
        }
        recurringResumeEvent = true
    }

    /**
     * Called when the lifecycle owner stops. Notifies all registered listeners of the stop event.
     *
     * @param owner The lifecycle owner that stopped
     */
    override fun onStop(owner: LifecycleOwner) {
        scope.launch { listeners.forEach { it.onStop() } }
    }

    /**
     * Checks if the lifecycle is currently in the RESUMED state.
     *
     * @return `true` if the lifecycle is at least RESUMED, `false` otherwise
     */
    public fun isResumed(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    /**
     * Subscribes a listener to receive lifecycle events. If this is the first listener, the
     * observer will start observing the lifecycle.
     *
     * @param listener The [LifecycleListener] to add
     */
    public suspend fun subscribe(listener: LifecycleListener) {
        listeners.add(listener)
        if (isActive.compareAndSet(false, true)) {
            recurringResumeEvent = false
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@StreamLifecycleObserver) }
        }
    }

    /**
     * Unsubscribes a listener from receiving lifecycle events. If this was the last listener, the
     * observer will stop observing the lifecycle.
     *
     * @param listener The [LifecycleListener] to remove
     */
    public suspend fun unsubscribe(listener: LifecycleListener) {
        listeners.remove(listener)
        if (listeners.isEmpty() && isActive.compareAndSet(true, false)) {
            withContext(Dispatchers.Main) { lifecycle.removeObserver(this@StreamLifecycleObserver) }
        }
    }

    /** Interface for objects that want to be notified of lifecycle events. */
    public interface LifecycleListener {
        /** Called when the lifecycle owner resumes. */
        public suspend fun onResume()

        /** Called when the lifecycle owner stops. */
        public suspend fun onStop()
    }
}
