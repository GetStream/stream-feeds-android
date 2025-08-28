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

import io.getstream.android.core.network.NetworkStateProvider
import io.getstream.feeds.android.client.internal.client.reconnect.lifecycle.StreamLifecycleObserver
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class AutomaticReconnectionPolicyTest {

    @Test
    fun `InternetAvailabilityReconnectionPolicy on network connected, then shouldReconnect returns true`() {
        val networkStateProvider: NetworkStateProvider = mockk()
        every { networkStateProvider.isConnected() } returns true

        val policy = InternetAvailabilityReconnectionPolicy(networkStateProvider)

        assertTrue(policy.shouldReconnect())
    }

    @Test
    fun `InternetAvailabilityReconnectionPolicy on network disconnected, then shouldReconnect returns false`() {
        val networkStateProvider: NetworkStateProvider = mockk()
        every { networkStateProvider.isConnected() } returns false

        val policy = InternetAvailabilityReconnectionPolicy(networkStateProvider)

        assertFalse(policy.shouldReconnect())
    }

    @Test
    fun `BackgroundStateReconnectionPolicy on app resumed, then shouldReconnect returns true`() {
        val lifecycleObserver: StreamLifecycleObserver = mockk()
        every { lifecycleObserver.isResumed() } returns true

        val policy = BackgroundStateReconnectionPolicy(lifecycleObserver)

        assertTrue(policy.shouldReconnect())
    }

    @Test
    fun `BackgroundStateReconnectionPolicy on app not resumed, then shouldReconnect returns false`() {
        val lifecycleObserver: StreamLifecycleObserver = mockk()
        every { lifecycleObserver.isResumed() } returns false

        val policy = BackgroundStateReconnectionPolicy(lifecycleObserver)

        assertFalse(policy.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with AND operator on all policies true, then shouldReconnect returns true`() {
        val policy1: AutomaticReconnectionPolicy = mockk()
        val policy2: AutomaticReconnectionPolicy = mockk()
        val policy3: AutomaticReconnectionPolicy = mockk()
        every { policy1.shouldReconnect() } returns true
        every { policy2.shouldReconnect() } returns true
        every { policy3.shouldReconnect() } returns true

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.AND,
            listOf(policy1, policy2, policy3)
        )

        assertTrue(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with AND operator on one policy false, then shouldReconnect returns false`() {
        val policy1: AutomaticReconnectionPolicy = mockk()
        val policy2: AutomaticReconnectionPolicy = mockk()
        val policy3: AutomaticReconnectionPolicy = mockk()
        every { policy1.shouldReconnect() } returns true
        every { policy2.shouldReconnect() } returns false // One false
        every { policy3.shouldReconnect() } returns true

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.AND,
            listOf(policy1, policy2, policy3)
        )

        assertFalse(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with AND operator on all policies false, then shouldReconnect returns false`() {
        val policy1: AutomaticReconnectionPolicy = mockk()
        val policy2: AutomaticReconnectionPolicy = mockk()
        every { policy1.shouldReconnect() } returns false
        every { policy2.shouldReconnect() } returns false

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.AND,
            listOf(policy1, policy2)
        )

        assertFalse(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with OR operator on all policies true, then shouldReconnect returns true`() {
        val policy1: AutomaticReconnectionPolicy = mockk()
        val policy2: AutomaticReconnectionPolicy = mockk()
        every { policy1.shouldReconnect() } returns true
        every { policy2.shouldReconnect() } returns true

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.OR,
            listOf(policy1, policy2)
        )

        assertTrue(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with OR operator on one policy true, then shouldReconnect returns true`() {
        val policy1: AutomaticReconnectionPolicy = mockk()
        val policy2: AutomaticReconnectionPolicy = mockk()
        val policy3: AutomaticReconnectionPolicy = mockk()
        every { policy1.shouldReconnect() } returns false
        every { policy2.shouldReconnect() } returns true // One true is enough
        every { policy3.shouldReconnect() } returns false

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.OR,
            listOf(policy1, policy2, policy3)
        )

        assertTrue(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with OR operator on all policies false, then shouldReconnect returns false`() {
        val policy1: AutomaticReconnectionPolicy = mockk()
        val policy2: AutomaticReconnectionPolicy = mockk()
        every { policy1.shouldReconnect() } returns false
        every { policy2.shouldReconnect() } returns false

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.OR,
            listOf(policy1, policy2)
        )

        assertFalse(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with AND operator on empty list, then shouldReconnect returns true`() {
        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.AND,
            emptyList()
        )

        // AND with empty list should return true (all of nothing is true)
        assertTrue(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with OR operator on empty list, then shouldReconnect returns false`() {
        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.OR,
            emptyList()
        )

        // OR with empty list should return false (any of nothing is false)
        assertFalse(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with single policy AND, then delegates correctly`() {
        val policy: AutomaticReconnectionPolicy = mockk()
        every { policy.shouldReconnect() } returns true

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.AND,
            listOf(policy)
        )

        assertTrue(composite.shouldReconnect())
    }

    @Test
    fun `CompositeReconnectionPolicy with single policy OR, then delegates correctly`() {
        val policy: AutomaticReconnectionPolicy = mockk()
        every { policy.shouldReconnect() } returns false

        val composite = CompositeReconnectionPolicy(
            CompositeReconnectionPolicy.Operator.OR,
            listOf(policy)
        )

        assertFalse(composite.shouldReconnect())
    }
}
