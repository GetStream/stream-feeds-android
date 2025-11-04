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

import io.getstream.feeds.android.network.models.AIImageConfig
import io.getstream.feeds.android.network.models.AITextConfig
import io.getstream.feeds.android.network.models.AIVideoConfig
import io.getstream.feeds.android.network.models.AutomodPlatformCircumventionConfig
import io.getstream.feeds.android.network.models.AutomodSemanticFiltersConfig
import io.getstream.feeds.android.network.models.AutomodToxicityConfig
import io.getstream.feeds.android.network.models.BlockListConfig
import io.getstream.feeds.android.network.models.VelocityFilterConfig
import java.util.Date

public data class ModerationConfigData(
    public val aiImageConfig: AIImageConfig?,
    public val aiTextConfig: AITextConfig?,
    public val aiVideoConfig: AIVideoConfig?,
    public val async: Boolean,
    public val automodPlatformCircumventionConfig: AutomodPlatformCircumventionConfig?,
    public val automodSemanticFiltersConfig: AutomodSemanticFiltersConfig?,
    public val automodToxicityConfig: AutomodToxicityConfig?,
    public val blockListConfig: BlockListConfig?,
    public val createdAt: Date,
    public val key: String,
    public val team: String,
    public val updatedAt: Date,
    public val velocityFilterConfig: VelocityFilterConfig?,
) {
    public val id: String
        get() = key
}
