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

import io.getstream.feeds.android.client.api.file.FeedUploader

/**
 * Configuration class for the Stream Feeds Android client.
 *
 * This class contains all the configuration options needed to customize the behavior of the Stream
 * Feeds client, including push notifications settings.
 *
 * @param pushNotificationsConfig Configuration for push notifications behavior. Defaults to
 *   [PushNotificationsConfig] with default settings.
 */
public class FeedsConfig(
    public val customUploader: FeedUploader? = null,
    public val pushNotificationsConfig: PushNotificationsConfig = PushNotificationsConfig(),
)
