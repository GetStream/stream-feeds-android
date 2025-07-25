package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.core.generated.models.ModerationV2Response

/**
 * Model representing content moderation results from Stream's moderation system.
 *
 * @property action The moderation action that was taken on the content.
 * @property originalText The original text content that was analyzed for moderation.
 * @property blocklistMatched The name of the blocklist that was matched during moderation, if any.
 * @property platformCircumvented Whether platform circumvention was detected in the content.
 * @property semanticFilterMatched The name of the semantic filter that was matched during moderation, if any.
 * @property imageHarms A list of image-related harms that were detected in the content.
 * @property textHarms A list of text-related harms that were detected in the content.
 */
public data class Moderation(
    public val action: String,
    public val originalText: String,
    public val blocklistMatched: String?,
    public val platformCircumvented: Boolean?,
    public val semanticFilterMatched: String?,
    public val imageHarms: List<String>,
    public val textHarms: List<String>,
)

/**
 * Extension function to convert a [ModerationV2Response] to a [Moderation] model.
 */
internal fun ModerationV2Response.toModel(): Moderation = Moderation(
    action = action,
    originalText = originalText,
    blocklistMatched = blocklistMatched,
    platformCircumvented = platformCircumvented,
    semanticFilterMatched = semanticFilterMatched,
    imageHarms = imageHarms ?: emptyList(),
    textHarms = textHarms ?: emptyList(),
)
