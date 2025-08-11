package io.getstream.feeds.android.client.api.file

import io.getstream.feeds.android.client.api.model.FeedId

/**
 * The context for uploading feed-related files. It's an interface so that clients can implement multiple
 * upload contexts if needed and share the same uploader.
 */
public interface FeedUploadContext : UploadContext {
    public val feedId: FeedId
}

/**
 * Default implementation of [FeedUploadContext] as a data class.
 *
 * This class is used when you want to upload files to a specific feed without additional context.
 *
 * @property feedId The ID of the feed to which the file will be uploaded.
 */
public data class DefaultFeedUploadContext(override val feedId: FeedId) : FeedUploadContext

public typealias FeedUploadPayload = UploadPayload<FeedUploadContext>

public typealias FeedUploader = Uploader<FeedUploadContext>
