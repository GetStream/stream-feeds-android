package io.getstream.feeds.android.client.api.file

/**
 * A marker interface for providing metadata for a file upload.
 *
 * Since different features (e.g., chat, feed posts) may require different metadata to be sent
 * along with a file, this interface allows for the creation of feature-specific context objects.
 *
 * @see UploadPayload.context
 */
public interface UploadContext
