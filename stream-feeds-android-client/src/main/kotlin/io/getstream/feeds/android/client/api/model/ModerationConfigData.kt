package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.AIImageConfig
import io.getstream.feeds.android.core.generated.models.AITextConfig
import io.getstream.feeds.android.core.generated.models.AIVideoConfig
import io.getstream.feeds.android.core.generated.models.AutomodPlatformCircumventionConfig
import io.getstream.feeds.android.core.generated.models.AutomodSemanticFiltersConfig
import io.getstream.feeds.android.core.generated.models.AutomodToxicityConfig
import io.getstream.feeds.android.core.generated.models.BlockListConfig
import io.getstream.feeds.android.core.generated.models.ConfigResponse
import io.getstream.feeds.android.core.generated.models.VelocityFilterConfig
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

/**
 * Maps [ConfigResponse] to [ModerationConfigData].
 */
internal fun ConfigResponse.toModel(): ModerationConfigData = ModerationConfigData(
    aiImageConfig = aiImageConfig,
    aiTextConfig = aiTextConfig,
    aiVideoConfig = aiVideoConfig,
    async = async,
    automodPlatformCircumventionConfig = automodPlatformCircumventionConfig,
    automodSemanticFiltersConfig = automodSemanticFiltersConfig,
    automodToxicityConfig = automodToxicityConfig,
    blockListConfig = blockListConfig,
    createdAt = createdAt.toDate(),
    key = key,
    team = team,
    updatedAt = updatedAt.toDate(),
    velocityFilterConfig = velocityFilterConfig,
)
