package io.getstream.feeds.android.client.internal.repository

/**
 * Repository for managing files on the server.
 */
internal interface FilesRepository {

    /**
     * Deletes a file from the server using its URL.
     *
     * @param url The URL of the file to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    suspend fun deleteFile(url: String): Result<Unit>

    /**
     * Deletes an image from the server using its URL.
     *
     * @param url The URL of the image to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    suspend fun deleteImage(url: String): Result<Unit>
}
