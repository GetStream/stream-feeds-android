package io.getstream.feeds.android.client.api.file

/**
 * The Uploader is responsible for uploading files.
 */
public interface Uploader<C : UploadContext> {
    /**
     * Uploads a file and returns the information of the uploaded remote file.
     *
     * @param payload The payload containing the information about the file to upload.
     * @param progress A lambda that notifies about upload progress (from 0.0 to 1.0).
     * @return A [Result] containing the uploaded file's information or an error.
     */
    public suspend fun upload(
        payload: UploadPayload<C>,
        progress: ((Double) -> Unit)? = null,
    ): Result<UploadedFile>
}
