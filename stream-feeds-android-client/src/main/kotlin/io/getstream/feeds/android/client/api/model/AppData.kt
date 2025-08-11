package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.core.generated.models.AppResponseFields

/**
 * Data class representing application data configuration.
 *
 * @property asyncUrlEnrichEnabled Whether async URL enrichment is enabled.
 * @property autoTranslationEnabled Whether auto translation is enabled.
 * @property fileUploadConfig Configuration for file uploads.
 * @property imageUploadConfig Configuration for image uploads.
 * @property name Name of the application.
 */
public data class AppData(
    val asyncUrlEnrichEnabled: Boolean,
    val autoTranslationEnabled: Boolean,
    val fileUploadConfig: FileUploadConfigData,
    val imageUploadConfig: FileUploadConfigData,
    val name: String
)

/**
 * Converts [AppResponseFields] to [AppData].
 */
internal fun AppResponseFields.toModel(): AppData = AppData(
    asyncUrlEnrichEnabled = asyncUrlEnrichEnabled,
    autoTranslationEnabled = autoTranslationEnabled,
    fileUploadConfig = fileUploadConfig.toModel(),
    imageUploadConfig = imageUploadConfig.toModel(),
    name = name,
)