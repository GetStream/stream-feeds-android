package io.getstream.feeds.android.client.api.model

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
