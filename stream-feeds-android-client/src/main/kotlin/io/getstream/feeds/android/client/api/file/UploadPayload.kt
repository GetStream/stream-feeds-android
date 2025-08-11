package io.getstream.feeds.android.client.api.file

import java.io.File

/**
 * Represents a complete, self-contained request for a file upload operation.
 *
 * This class encapsulates all the necessary information for the uploader: the file itself,
 * its type, and the contextual metadata required to process it.
 *
 * @property file The local [File] object on disk that is to be uploaded.
 * @property type The semantic type of the file, represented by the [FileType] sealed interface.
 * This helps differentiate between images, videos, documents, etc.
 * @property context The metadata required to process the upload.
 */
public data class UploadPayload<C : UploadContext>(
    val file: File,
    val type: FileType,
    val context: C,
)
