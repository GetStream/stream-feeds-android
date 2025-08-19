package io.getstream.feeds.android.client.api.file

import java.io.File

/**
 * The context for uploading feed-related files. It's an interface so that clients can implement multiple
 * upload contexts if needed and share the same uploader.
 */
public interface FeedUploadContext : UploadContext

/**
 * Default implementation of [FeedUploadContext] to be used when you want to upload files without
 * additional context.
 */
public data object EmptyFeedUploadContext : FeedUploadContext

public typealias FeedUploadPayload = UploadPayload<FeedUploadContext>

public fun FeedUploadPayload(file: File, type: FileType): FeedUploadPayload =
    FeedUploadPayload(file, type, EmptyFeedUploadContext)

public typealias FeedUploader = Uploader<FeedUploadContext>
