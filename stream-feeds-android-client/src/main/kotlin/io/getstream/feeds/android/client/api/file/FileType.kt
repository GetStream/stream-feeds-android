package io.getstream.feeds.android.client.api.file

/**
 * A sealed interface representing the semantic category of a file.
 *
 * This is used to differentiate between file types that may require different handling
 * or validation, such as images versus other generic files.
 */
public sealed interface FileType {
    public data class Image(val format: String) : FileType
    public data class Other(val format: String) : FileType
}

/**
 * A convenience extension property to check if a [FileType] is an image.
 *
 * @return `true` if this instance is an image, `false` otherwise.
 */
public val FileType.isImage
    get() = this is FileType.Image
