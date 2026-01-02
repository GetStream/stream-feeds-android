/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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
import io.getstream.feeds.android.client.api.logging.LoggingConfig

/**
 * Configuration class for the Stream Feeds Android client. This class contains all the
 * configuration options needed to customize the behavior of the Stream Feeds client, such as the
 * option for customizing the CDN.
 *
 * @param customUploader Optional [FeedUploader] implementation for overriding the default CDN.
 * @param loggingConfig Configuration for logging within the FeedsClient. See [LoggingConfig] for
 *   more details.
 */
public class FeedsConfig(
    public val customUploader: FeedUploader? = null,
    public val loggingConfig: LoggingConfig = LoggingConfig(),
)
