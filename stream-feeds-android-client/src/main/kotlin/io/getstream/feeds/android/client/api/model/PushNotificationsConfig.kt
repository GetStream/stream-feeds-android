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
package io.getstream.feeds.android.client.api.model

// import io.getstream.android.push.PushDeviceGenerator

/**
 * Configuration class for push notifications in the Stream Feeds Android client.
 *
 * This class holds the configuration needed to set up push notifications for the Stream Feeds
 * client, including the list of push device generators that will be used to generate push tokens
 * for different push notification providers (FCM, HMS, etc.).
 *
 * @param pushDeviceGenerators List of [PushDeviceGenerator] instances that will be used to generate
 *   push tokens for different push notification providers. Defaults to an empty list if not
 *   specified.
 */
public class PushNotificationsConfig(
    public val pushDeviceGenerators: List<String> = emptyList() // TODO: Rework this
)
