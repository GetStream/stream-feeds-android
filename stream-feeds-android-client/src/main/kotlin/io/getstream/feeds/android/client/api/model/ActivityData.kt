package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.core.generated.models.ActivityLocation
import io.getstream.feeds.android.core.generated.models.ActivityResponse
import io.getstream.feeds.android.core.generated.models.Attachment
import java.util.Date
import kotlin.math.max

/**
 * A data model representing an activity in the Stream Feeds system.
 *
 * This class contains all the information about an activity, including its content,
 * metadata, reactions, comments, bookmarks, and user information. It supports
 * hierarchical activities with parent-child relationships and real-time interaction tracking.
 *
 * Features:
 * - Hierarchical Activities: Supports parent-child activity relationships
 * - Reactions: Tracks user reactions and reaction counts
 * - Comments: Supports comment threads and comment counts
 * - Bookmarks: Tracks user bookmarks and bookmark counts
 * - Mentions: Supports user mentions with metadata
 * - Attachments: Supports file and media attachments
 * - Polls: Supports embedded poll functionality
 * - Moderation: Includes moderation and scoring data
 * - Real-time Updates: Designed for real-time state management
 *
 * @property attachments File attachments associated with the activity. This property contains any
 * files, images, or other media attached to the activity.
 * @property bookmarkCount The number of bookmarks this activity has received.
 * @property commentCount The total number of comments on this activity.
 * @property comments The comments associated with this activity. This property contains the list
 * of comments, which may be paginated or limited based on loading strategy.
 * @property createdAt The date and time when the activity was created.
 * @property currentFeed The current feed context for this activity. This property indicates which
 * feed this activity is being viewed from, if applicable.
 * @property custom Custom data associated with the activity. This property allows for storing
 * additional metadata or custom fields specific to your application's needs.
 * @property deletedAt The date and time when the activity was deleted, if applicable. If the activity
 * has been deleted, this property contains the deletion timestamp. If the activity is still active,
 * this property is `null`.
 * @property editedAt The date and time when the activity was last edited, if applicable.
 * @property expiresAt The date and time when the activity expires, if applicable. This is used
 * for temporary or time-limited activities.
 * @property feeds The list of feed IDs where this activity appears. An activity can appear in
 * multiple feeds simultaneously.
 * @property filterTags Tags used for content filtering and categorization.
 * @property id The unique identifier of the activity.
 * @property interestTags Tags indicating user interests or content categories for recommendation purposes.
 * @property latestReactions The most recent reactions added to the activity. This property contains
 * the latest reactions from users, typically limited to the most recent ones.
 * @property location Geographic location data associated with the activity, if any.
 * @property mentionedUsers Users mentioned in the activity. This property contains the list of users
 * who were mentioned in the activity using @mentions or similar functionality.
 * @property moderation Moderation state and data for the activity.
 * @property notificationContext Contextual data for notifications related to this activity.
 * @property ownBookmarks All the bookmarks from the current user for this activity.
 * @property ownReactions All the reactions from the current user for this activity.
 * @property parent The parent activity, if this is a child activity. This supports hierarchical
 * activity structures where activities can be replies or responses to other activities.
 * @property poll Poll data if this activity contains a poll. This property contains all poll-related
 * information including options, votes, and metadata.
 * @property popularity A popularity score for the activity, typically based on engagement metrics.
 * @property reactionCount The total number of reactions on the activity across all reaction types.
 * @property reactionGroups Groups of reactions by type. This property organizes reactions by their
 * type (e.g., "like", "love", "laugh") and provides counts and metadata for each reaction type.
 * @property score A relevance or quality score assigned to the activity. This score is typically
 * used for ranking, sorting, or algorithmic feed placement.
 * @property searchData Additional data used for search indexing and retrieval. This property
 * contains metadata that helps with search functionality.
 * @property shareCount The number of times this activity has been shared.
 * @property text The text content of the activity. This property contains the main text content
 * of the activity. It may be `null` for activities that only contain media or other content types.
 * @property type The type or category of the activity (e.g., "post", "share", "like").
 * @property updatedAt The date and time when the activity was last updated.
 * @property user The user who created the activity.
 * @property visibility The visibility settings for the activity (e.g., public, private, followers).
 * @property visibilityTag Additional visibility classification tag, if applicable.
 */
public data class ActivityData(
    val attachments: List<Attachment>,
    val bookmarkCount: Int,
    val commentCount: Int,
    val comments: List<CommentData>,
    val createdAt: Date,
    val currentFeed: FeedData?,
    val custom: Map<String, Any?>,
    val deletedAt: Date?,
    val editedAt: Date?,
    val expiresAt: Date?,
    val feeds: List<String>,
    val filterTags: List<String>,
    val id: String,
    val interestTags: List<String>,
    val latestReactions: List<FeedsReactionData>,
    val location: ActivityLocation?,
    val mentionedUsers: List<UserData>,
    val moderation: Moderation?,
    val notificationContext: Map<String, Any?>?,
    val ownBookmarks: List<BookmarkData>,
    val ownReactions: List<FeedsReactionData>,
    val parent: ActivityData?,
    val poll: PollData?,
    val popularity: Int,
    val reactionCount: Int,
    val reactionGroups: Map<String, ReactionGroupData>,
    val score: Float,
    val searchData: Map<String, Any?>,
    val shareCount: Int,
    val text: String?,
    val type: String,
    val updatedAt: Date,
    val user: UserData,
    val visibility: ActivityDataVisibility,
    val visibilityTag: String?
)

/**
 * Type alias for activity visibility settings.
 * This represents the visibility state of an activity (public, private or tag).
 */
public sealed class ActivityDataVisibility(public val value: String) {
    public object Private : ActivityDataVisibility("private")
    public object Public : ActivityDataVisibility("public")
    public object Tag : ActivityDataVisibility("tag")
    public data class Unknown(val unknownValue: String) : ActivityDataVisibility(unknownValue)
}

/**
 * Converts an [ActivityResponse] to an [ActivityData] model.
 */
internal fun ActivityResponse.toModel(): ActivityData = ActivityData(
    attachments = attachments,
    bookmarkCount = bookmarkCount,
    commentCount = commentCount,
    comments = comments.map { it.toModel() },
    createdAt = createdAt.toDate(),
    currentFeed = currentFeed?.toModel(),
    custom = custom,
    deletedAt = deletedAt?.let { Date(it.toInstant().toEpochMilli()) },
    editedAt = editedAt?.let { Date(it.toInstant().toEpochMilli()) },
    expiresAt = expiresAt?.let { Date(it.toInstant().toEpochMilli()) },
    feeds = feeds,
    filterTags = filterTags,
    id = id,
    interestTags = interestTags,
    latestReactions = latestReactions.map { it.toModel() },
    location = location,
    mentionedUsers = mentionedUsers.map { it.toModel() },
    moderation = moderation?.toModel(),
    notificationContext = notificationContext,
    ownBookmarks = ownBookmarks.map { it.toModel() },
    ownReactions = ownReactions.map { it.toModel() },
    parent = parent?.toModel(),
    poll = poll?.toModel(),
    popularity = popularity,
    reactionCount = reactionCount,
    reactionGroups = reactionGroups.mapValues { it.value.toModel() },
    score = score,
    searchData = searchData,
    shareCount = shareCount,
    text = text,
    type = type,
    updatedAt = updatedAt.toDate(),
    user = user.toModel(),
    visibility = visibility.toModel(),
    visibilityTag = visibilityTag,
)

/**
 * Converts a [ActivityDataVisibility] to a [ActivityDataVisibility].
 */
internal fun ActivityResponse.Visibility.toModel(): ActivityDataVisibility = when (this) {
    ActivityResponse.Visibility.Private -> ActivityDataVisibility.Private
    ActivityResponse.Visibility.Public -> ActivityDataVisibility.Public
    ActivityResponse.Visibility.Tag -> ActivityDataVisibility.Tag
    is ActivityResponse.Visibility.Unknown -> ActivityDataVisibility.Unknown(unknownValue)
}

/**
 * Adds a comment to the activity, updating the comment count and the list of comments.
 *
 * @param comment The comment to be added.
 * @return A new [ActivityData] instance with the updated comments and comment count.
 */
internal fun ActivityData.addComment(comment: CommentData): ActivityData {
    val updatedComments = this.comments.upsert(comment, CommentData::id)
    val updatedCommentCount = if (updatedComments.size > this.comments.size) {
        this.commentCount + 1
    } else {
        this.commentCount
    }
    return this.copy(
        comments = updatedComments,
        commentCount = updatedCommentCount,
    )
}

/**
 * Removes a comment from the activity, updating the comment count and the list of comments.
 *
 * @param comment The comment to be removed.
 * @return A new [ActivityData] instance with the updated comments and comment count.
 */
internal fun ActivityData.removeComment(comment: CommentData): ActivityData {
    val updatedComments = this.comments.filter { it.id != comment.id }
    return this.copy(
        comments = updatedComments,
        commentCount = max(0, this.commentCount - 1),
    )
}

/**
 * Adds a bookmark to the activity, updating the own bookmarks and bookmark count.
 *
 * @param bookmark The bookmark to be added.
 * @param currentUserId The ID of the current user, used to determine if the bookmark belongs to
 * them.
 * @return A new [ActivityData] instance with the updated own bookmarks and bookmark count.
 */
internal fun ActivityData.addBookmark(bookmark: BookmarkData, currentUserId: String): ActivityData {
    val updatedOwnBookmarks = if (bookmark.user.id == currentUserId) {
        this.ownBookmarks.upsert(bookmark, BookmarkData::id)
    } else {
        this.ownBookmarks
    }
    return this.copy(
        ownBookmarks = updatedOwnBookmarks,
        bookmarkCount = this.bookmarkCount + 1,
    )
}

/**
 * Deletes a bookmark from the activity, updating the own bookmarks and bookmark count.
 *
 * @param bookmark The bookmark to be deleted.
 * @param currentUserId The ID of the current user, used to determine if the bookmark belongs to
 * them.
 * @return A new [ActivityData] instance with the updated own bookmarks and bookmark count.
 */
internal fun ActivityData.deleteBookmark(
    bookmark: BookmarkData,
    currentUserId: String
): ActivityData {
    val updatedOwnBookmarks = if (bookmark.user.id == currentUserId) {
        this.ownBookmarks.filter { it.id != bookmark.id }
    } else {
        this.ownBookmarks
    }
    return this.copy(
        ownBookmarks = updatedOwnBookmarks,
        bookmarkCount = max(0, this.bookmarkCount - 1),
    )
}

/**
 * Adds a reaction to the activity, updating the latest reactions, reaction groups,
 * reaction count, and own reactions.
 *
 * @param reaction The reaction to be added.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to.
 * @return A new [ActivityData] instance with the updated reactions and counts.
 */
internal fun ActivityData.addReaction(
    reaction: FeedsReactionData,
    currentUserId: String
): ActivityData {
    val updatedLatestReactions = this.latestReactions.upsert(reaction, FeedsReactionData::id)
    val reactionGroup = this.reactionGroups[reaction.type]
        ?: ReactionGroupData(1, reaction.createdAt, reaction.createdAt)
    val updatedReactionGroup = reactionGroup.increment(reaction.createdAt)
    val updatedReactionGroups = this.reactionGroups.toMutableMap().apply {
        this[reaction.type] = updatedReactionGroup
    }
    val updatedReactionCount = updatedReactionGroups.values.sumOf(ReactionGroupData::count)
    val updatedOwnReactions = if (reaction.user.id == currentUserId) {
        this.ownReactions.upsert(reaction, FeedsReactionData::id)
    } else {
        this.ownReactions
    }
    return this.copy(
        latestReactions = updatedLatestReactions,
        reactionGroups = updatedReactionGroups,
        reactionCount = updatedReactionCount,
        ownReactions = updatedOwnReactions,
    )
}

/**
 * Removes a reaction from the activity, updating the latest reactions, reaction groups,
 * reaction count, and own reactions.
 *
 * @param reaction The reaction to be removed.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to.
 * @return A new [ActivityData] instance with the updated reactions and counts.
 */
internal fun ActivityData.removeReaction(
    reaction: FeedsReactionData,
    currentUserId: String
): ActivityData {
    val updatedLatestReactions = this.latestReactions.filter { it.id != reaction.id }
    val updatedOwnReactions = if (reaction.user.id == currentUserId) {
        this.ownReactions.filter { it.id != reaction.id }
    } else {
        this.ownReactions
    }
    val reactionGroup = this.reactionGroups[reaction.type]
    if (reactionGroup == null) {
        // If there is no reaction group for this type, just update latest and own reactions.
        // Note: This is only a hypothetical case, as we should always have a reaction group.
        return this.copy(
            latestReactions = updatedLatestReactions,
            ownReactions = updatedOwnReactions,
        )
    }
    val updatedReactionGroup = reactionGroup.decrement(reaction.createdAt)
    val updatedReactionGroups = if (updatedReactionGroup.isEmpty) {
        this.reactionGroups - reaction.type // Remove empty group
    } else {
        this.reactionGroups.toMutableMap().apply {
            this[reaction.type] = updatedReactionGroup
        }
    }
    val updatedReactionCount = updatedReactionGroups.values.sumOf(ReactionGroupData::count)
    return this.copy(
        latestReactions = updatedLatestReactions,
        reactionGroups = updatedReactionGroups,
        reactionCount = updatedReactionCount,
        ownReactions = updatedOwnReactions,
    )
}
