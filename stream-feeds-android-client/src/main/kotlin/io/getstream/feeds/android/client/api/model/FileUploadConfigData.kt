package io.getstream.feeds.android.client.api.model

/**
 * Data class representing file upload configuration.
 *
 * @property allowedFileExtensions List of allowed file extensions.
 * @property allowedMimeTypes List of allowed MIME types.
 * @property blockedFileExtensions List of blocked file extensions.
 * @property blockedMimeTypes List of blocked MIME types.
 * @property sizeLimit Maximum allowed file size in bytes.
 */
public data class FileUploadConfigData(
    val allowedFileExtensions: List<String>,
    val allowedMimeTypes: List<String>,
    val blockedFileExtensions: List<String>,
    val blockedMimeTypes: List<String>,
    val sizeLimit: Int
)

