/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.client.internal.test

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityDataVisibility
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedMemberStatus
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FileUploadConfigData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.FollowStatus
import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.ReactionGroupData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.network.models.AcceptFeedMemberInviteResponse
import io.getstream.feeds.android.network.models.AcceptFollowResponse
import io.getstream.feeds.android.network.models.ActivityResponse
import io.getstream.feeds.android.network.models.BookmarkFolderResponse
import io.getstream.feeds.android.network.models.BookmarkResponse
import io.getstream.feeds.android.network.models.CommentResponse
import io.getstream.feeds.android.network.models.DeleteCommentResponse
import io.getstream.feeds.android.network.models.FeedMemberResponse
import io.getstream.feeds.android.network.models.FeedResponse
import io.getstream.feeds.android.network.models.FeedsReactionResponse
import io.getstream.feeds.android.network.models.FollowResponse
import io.getstream.feeds.android.network.models.GetFollowSuggestionsResponse
import io.getstream.feeds.android.network.models.GetOrCreateFeedResponse
import io.getstream.feeds.android.network.models.PinActivityResponse
import io.getstream.feeds.android.network.models.PollResponseData
import io.getstream.feeds.android.network.models.PollVoteResponseData
import io.getstream.feeds.android.network.models.QueryFeedMembersResponse
import io.getstream.feeds.android.network.models.QueryFeedsResponse
import io.getstream.feeds.android.network.models.QueryFollowsResponse
import io.getstream.feeds.android.network.models.RejectFeedMemberInviteResponse
import io.getstream.feeds.android.network.models.RejectFollowResponse
import io.getstream.feeds.android.network.models.SingleFollowResponse
import io.getstream.feeds.android.network.models.UnpinActivityResponse
import io.getstream.feeds.android.network.models.UpdateFeedMembersResponse
import io.getstream.feeds.android.network.models.UpdateFeedResponse
import io.getstream.feeds.android.network.models.UserResponse
import java.util.Date

internal object TestData {
    fun commentData(
        id: String = "comment-id",
        text: String = "Test comment",
        objectId: String? = null,
        objectType: String = "comment",
        createdAt: Date = Date(1),
        ownReactions: List<FeedsReactionData> = emptyList(),
        parentId: String? = null,
    ) =
        CommentData(
            id = id,
            parentId = parentId,
            attachments = null,
            confidenceScore = 0f,
            controversyScore = null,
            createdAt = createdAt,
            custom = null,
            deletedAt = null,
            downvoteCount = 0,
            latestReactions = emptyList(),
            mentionedUsers = emptyList(),
            moderation = null,
            objectId = objectId ?: id,
            objectType = objectType,
            ownReactions = ownReactions,
            reactionCount = 0,
            reactionGroups = emptyMap(),
            replyCount = 0,
            score = 0,
            status = "",
            text = text,
            updatedAt = Date(1),
            upvoteCount = 0,
            user = userData(id),
        )

    fun threadedCommentData(
        id: String,
        parentId: String? = null,
        text: String = "Test comment",
        replies: List<ThreadedCommentData> = emptyList(),
        createdAt: Date = Date(1),
        latestReactions: List<FeedsReactionData> = emptyList(),
        ownReactions: List<FeedsReactionData> = emptyList(),
        reactionCount: Int = 0,
        reactionGroups: Map<String, ReactionGroupData> = emptyMap(),
        replyCount: Int = replies.size,
    ): ThreadedCommentData {
        return ThreadedCommentData(
            id = id,
            parentId = parentId,
            attachments = null,
            confidenceScore = 0f,
            controversyScore = null,
            createdAt = createdAt,
            custom = null,
            deletedAt = null,
            downvoteCount = 0,
            latestReactions = latestReactions,
            mentionedUsers = emptyList(),
            meta = null,
            moderation = null,
            objectId = id,
            objectType = "comment",
            ownReactions = ownReactions,
            reactionCount = reactionCount,
            reactionGroups = reactionGroups,
            replies = replies,
            replyCount = replyCount,
            score = 0,
            status = "",
            text = text,
            updatedAt = Date(1),
            upvoteCount = 0,
            user = userData(id),
        )
    }

    fun feedsReactionData(
        activityId: String = "activity-1",
        type: String = "like",
        userId: String = "user-1",
        commentId: String? = null,
        createdAt: Date = Date(1000),
        updatedAt: Date = Date(1000),
    ): FeedsReactionData =
        FeedsReactionData(
            activityId = activityId,
            commentId = commentId,
            createdAt = createdAt,
            custom = null,
            type = type,
            updatedAt = updatedAt,
            user = userData(userId),
        )

    fun reactionGroupData(
        count: Int = 1,
        firstReactionAt: Date = Date(1000),
        lastReactionAt: Date = Date(1000),
    ): ReactionGroupData =
        ReactionGroupData(
            count = count,
            firstReactionAt = firstReactionAt,
            lastReactionAt = lastReactionAt,
        )

    fun userData(id: String): UserData =
        UserData(
            banned = false,
            blockedUserIds = emptyList(),
            createdAt = Date(1000),
            custom = emptyMap(),
            deactivatedAt = null,
            deletedAt = null,
            id = id,
            image = null,
            language = "en",
            lastActive = null,
            name = null,
            online = false,
            revokeTokensIssuedBefore = null,
            role = "user",
            teams = emptyList(),
            updatedAt = Date(1000),
        )

    fun activityData(
        id: String = "activity-1",
        text: String? = null,
        type: String = "post",
        poll: PollData? = null,
        ownBookmarks: List<BookmarkData> = emptyList(),
        ownReactions: List<FeedsReactionData> = emptyList(),
        comments: List<CommentData> = emptyList(),
        feeds: List<String> = emptyList(),
    ): ActivityData =
        ActivityData(
            attachments = emptyList(),
            bookmarkCount = 0,
            commentCount = comments.size,
            comments = comments,
            createdAt = Date(1000),
            currentFeed = null,
            custom = emptyMap(),
            deletedAt = null,
            editedAt = null,
            expiresAt = null,
            feeds = feeds,
            filterTags = emptyList(),
            id = id,
            interestTags = emptyList(),
            isWatched = null,
            latestReactions = emptyList(),
            location = null,
            mentionedUsers = emptyList(),
            moderation = null,
            notificationContext = null,
            ownBookmarks = ownBookmarks,
            ownReactions = ownReactions,
            parent = null,
            poll = poll,
            popularity = 0,
            reactionCount = 0,
            reactionGroups = emptyMap(),
            score = 0f,
            searchData = emptyMap(),
            shareCount = 0,
            text = text,
            type = type,
            updatedAt = Date(1000),
            user = userData("user-1"),
            visibility = ActivityDataVisibility.Public,
            visibilityTag = null,
        )

    fun aggregatedActivityData(
        activities: List<ActivityData> = listOf(activityData()),
        activityCount: Int = activities.size,
        createdAt: Date = Date(1000),
        group: String = "test-group",
        score: Float = 1.0f,
        updatedAt: Date = Date(1000),
        userCount: Int = 1,
        userCountTruncated: Boolean = false,
    ): AggregatedActivityData =
        AggregatedActivityData(
            activities = activities,
            activityCount = activityCount,
            createdAt = createdAt,
            group = group,
            score = score,
            updatedAt = updatedAt,
            userCount = userCount,
            userCountTruncated = userCountTruncated,
        )

    fun appData(name: String = "Test App"): AppData =
        AppData(
            asyncUrlEnrichEnabled = false,
            autoTranslationEnabled = false,
            fileUploadConfig = fileUploadConfigData(),
            imageUploadConfig = fileUploadConfigData(),
            name = name,
        )

    fun fileUploadConfigData(): FileUploadConfigData =
        FileUploadConfigData(
            allowedFileExtensions = emptyList(),
            allowedMimeTypes = emptyList(),
            blockedFileExtensions = emptyList(),
            blockedMimeTypes = emptyList(),
            sizeLimit = 0,
        )

    fun bookmarkData(activity: ActivityData): BookmarkData =
        BookmarkData(
            activity = activity,
            createdAt = Date(1000),
            custom = emptyMap(),
            folder = null,
            updatedAt = Date(1000),
            user = userData("user-1"),
        )

    fun bookmarkData(
        activityId: String = "activity-1",
        userId: String = "user-1",
        folder: BookmarkFolderData? = null,
        createdAt: Long = 1000,
    ): BookmarkData =
        BookmarkData(
            activity = activityData(activityId),
            createdAt = Date(createdAt),
            custom = emptyMap(),
            folder = folder,
            updatedAt = Date(1000),
            user = userData(userId),
        )

    fun bookmarkFolderData(
        id: String = "folder-1",
        name: String = "Test Folder",
    ): BookmarkFolderData =
        BookmarkFolderData(
            createdAt = Date(1000),
            custom = emptyMap(),
            id = id,
            name = name,
            updatedAt = Date(1000),
        )

    fun activityResponse(id: String = ""): ActivityResponse =
        ActivityResponse(
            bookmarkCount = 0,
            commentCount = 0,
            createdAt = Date(1000),
            id = id,
            popularity = 0,
            reactionCount = 0,
            score = 0f,
            shareCount = 0,
            type = "",
            updatedAt = Date(1000),
            visibility = ActivityResponse.Visibility.Public,
            user = userResponse(),
        )

    fun feedsReactionResponse() =
        FeedsReactionResponse(
            activityId = "activity-1",
            createdAt = Date(1000),
            type = "like",
            updatedAt = Date(1000),
            user = userResponse(),
        )

    fun pinActivityResponse(): PinActivityResponse =
        PinActivityResponse(
            createdAt = Date(1000),
            activity = activityResponse(),
            duration = "duration",
            feed = "feed",
            userId = "user",
        )

    fun unpinActivityResponse(): UnpinActivityResponse =
        UnpinActivityResponse(
            activity = activityResponse(),
            duration = "duration",
            feed = "feed",
            userId = "user",
        )

    fun activityPin(activity: ActivityData) =
        ActivityPinData(
            activity = activity,
            fid = FeedId("group:id"),
            userId = "user-1",
            createdAt = Date(1000),
            updatedAt = Date(1000),
        )

    fun userResponse() =
        UserResponse(
            id = "user-1",
            banned = false,
            createdAt = Date(1000),
            language = "en",
            online = false,
            role = "user",
            updatedAt = Date(1000),
        )

    fun bookmarkResponse() =
        BookmarkResponse(
            createdAt = Date(1000),
            updatedAt = Date(1000),
            activity = activityResponse(),
            user = userResponse(),
            custom = emptyMap(),
            folder = null,
        )

    fun bookmarkFolderResponse() =
        BookmarkFolderResponse(
            createdAt = Date(1000),
            custom = emptyMap(),
            id = "folder-1",
            name = "Test Folder",
            updatedAt = Date(1000),
        )

    fun followData(
        sourceFid: String = "user:user-1",
        targetFid: String = "user:user-2",
        createdAt: Date = Date(1000),
        updatedAt: Date = Date(1000),
    ): FollowData {
        val source = FeedId(sourceFid)
        val target = FeedId(targetFid)
        return FollowData(
            createdAt = createdAt,
            custom = emptyMap(),
            followerRole = "user",
            pushPreference = "all",
            requestAcceptedAt = createdAt,
            requestRejectedAt = null,
            sourceFeed =
                FeedData(
                    createdAt = createdAt,
                    createdBy = userData(source.id),
                    custom = emptyMap(),
                    deletedAt = null,
                    description = "Test feed",
                    fid = source,
                    filterTags = emptyList(),
                    followerCount = 0,
                    followingCount = 0,
                    groupId = "user",
                    id = source.id,
                    memberCount = 0,
                    ownCapabilities = emptyList(),
                    ownMembership = null,
                    name = "Test Feed",
                    pinCount = 0,
                    updatedAt = updatedAt,
                    visibility = "public",
                ),
            status = FollowStatus.Accepted,
            targetFeed =
                FeedData(
                    createdAt = createdAt,
                    createdBy = userData(target.id),
                    custom = emptyMap(),
                    deletedAt = null,
                    description = "Target feed",
                    fid = target,
                    filterTags = emptyList(),
                    followerCount = 0,
                    followingCount = 0,
                    groupId = "user",
                    id = target.id,
                    memberCount = 0,
                    ownCapabilities = emptyList(),
                    ownMembership = null,
                    name = "Target Feed",
                    pinCount = 0,
                    updatedAt = updatedAt,
                    visibility = "public",
                ),
            updatedAt = updatedAt,
        )
    }

    fun feedMemberData(
        userId: String = "user-1",
        role: String = "member",
        status: FeedMemberStatus = FeedMemberStatus.Member,
        createdAt: Date = Date(1000),
        updatedAt: Date = Date(1000),
    ): FeedMemberData =
        FeedMemberData(
            createdAt = createdAt,
            custom = emptyMap(),
            inviteAcceptedAt = createdAt,
            inviteRejectedAt = null,
            role = role,
            status = status,
            updatedAt = updatedAt,
            user = userData(userId),
        )

    fun pollOptionData(
        id: String = "option-1",
        text: String = "Test Option",
        custom: Map<String, Any?> = emptyMap(),
    ): PollOptionData = PollOptionData(custom = custom, id = id, text = text)

    fun pollVoteData(
        id: String = "vote-1",
        pollId: String = "poll-1",
        optionId: String = "option-1",
        userId: String = "user-1",
        answerText: String? = null,
    ): PollVoteData =
        PollVoteData(
            answerText = answerText,
            createdAt = Date(1000),
            id = id,
            isAnswer = answerText != null,
            optionId = optionId,
            pollId = pollId,
            updatedAt = Date(1000),
            user = userData(userId),
            userId = userId,
        )

    fun pollData(
        id: String = "poll-1",
        name: String = "Test Poll",
        description: String = "Test poll description",
        isClosed: Boolean = false,
        allowAnswers: Boolean = false,
        answersCount: Int = 0,
        latestAnswers: List<PollVoteData> = emptyList(),
        ownVotes: List<PollVoteData> = emptyList(),
        voteCount: Int = 0,
        latestVotesByOption: Map<String, List<PollVoteData>> = emptyMap(),
        voteCountsByOption: Map<String, Int> = emptyMap(),
        options: List<PollOptionData> = listOf(pollOptionData()),
    ): PollData =
        PollData(
            allowAnswers = allowAnswers,
            allowUserSuggestedOptions = false,
            answersCount = answersCount,
            createdAt = Date(1000),
            createdBy = userData("user-1"),
            createdById = "user-1",
            custom = emptyMap(),
            description = description,
            enforceUniqueVote = true,
            id = id,
            isClosed = isClosed,
            latestAnswers = latestAnswers,
            latestVotesByOption = latestVotesByOption,
            maxVotesAllowed = null,
            name = name,
            options = options,
            ownVotes = ownVotes,
            updatedAt = Date(1000),
            voteCount = voteCount,
            voteCountsByOption = voteCountsByOption,
            votingVisibility = "public",
        )

    fun pollResponseData(name: String = "Test Poll"): PollResponseData =
        PollResponseData(
            allowAnswers = false,
            allowUserSuggestedOptions = false,
            answersCount = 0,
            createdAt = Date(1000),
            createdById = "user-1",
            description = "Test poll description",
            enforceUniqueVote = true,
            id = "poll-1",
            name = name,
            updatedAt = Date(1000),
            voteCount = 0,
            votingVisibility = "public",
            latestAnswers = emptyList(),
            options = emptyList(),
            ownVotes = emptyList(),
            custom = emptyMap(),
            latestVotesByOption = emptyMap(),
            voteCountsByOption = emptyMap(),
            isClosed = false,
            maxVotesAllowed = null,
            createdBy = null,
        )

    fun feedData(
        id: String = "user-1",
        groupId: String = "user",
        name: String = "Test Feed",
        description: String = "Test feed description",
        createdAt: Long = 1000,
    ): FeedData =
        FeedData(
            createdAt = Date(createdAt),
            createdBy = userData(id),
            custom = emptyMap(),
            deletedAt = null,
            description = description,
            fid = FeedId("$groupId:$id"),
            filterTags = emptyList(),
            followerCount = 0,
            followingCount = 0,
            groupId = groupId,
            id = id,
            memberCount = 0,
            ownCapabilities = emptyList(),
            ownMembership = null,
            name = name,
            pinCount = 0,
            updatedAt = Date(1000),
            visibility = "public",
        )

    fun moderationConfigData(
        key: String = "config-1",
        team: String = "team-1",
        async: Boolean = false,
        createdAt: Date = Date(1000),
        updatedAt: Date = Date(1000),
    ): ModerationConfigData =
        ModerationConfigData(
            aiImageConfig = null,
            aiTextConfig = null,
            aiVideoConfig = null,
            async = async,
            automodPlatformCircumventionConfig = null,
            automodSemanticFiltersConfig = null,
            automodToxicityConfig = null,
            blockListConfig = null,
            createdAt = createdAt,
            key = key,
            team = team,
            updatedAt = updatedAt,
            velocityFilterConfig = null,
        )

    fun feedResponse() =
        FeedResponse(
            id = "feed-1",
            groupId = "user",
            name = "Test Feed",
            description = "Test feed description",
            feed = "user:feed-1",
            followerCount = 0,
            followingCount = 0,
            memberCount = 0,
            pinCount = 0,
            createdAt = Date(1000),
            updatedAt = Date(1000),
            createdBy = userResponse(),
            custom = emptyMap(),
        )

    fun followResponse() =
        FollowResponse(
            createdAt = Date(1000),
            updatedAt = Date(1000),
            sourceFeed = feedResponse(),
            targetFeed = feedResponse(),
            status = FollowResponse.Status.Accepted,
            pushPreference = FollowResponse.PushPreference.All,
            followerRole = "user",
            custom = emptyMap(),
        )

    fun feedMemberResponse() =
        FeedMemberResponse(
            createdAt = Date(1000),
            updatedAt = Date(1000),
            user = userResponse(),
            role = "member",
            status = FeedMemberResponse.Status.Member,
            custom = emptyMap(),
        )

    fun getOrCreateFeedResponse() =
        GetOrCreateFeedResponse(
            created = false,
            duration = "duration",
            activities = emptyList(),
            aggregatedActivities = emptyList(),
            followers = emptyList(),
            following = emptyList(),
            members = emptyList(),
            pinnedActivities = emptyList(),
            feed = feedResponse(),
            next = "next",
            prev = "prev",
        )

    fun queryFeedsResponse() =
        QueryFeedsResponse(
            duration = "duration",
            feeds = listOf(feedResponse()),
            next = "next",
            prev = "prev",
        )

    fun queryFollowsResponse() =
        QueryFollowsResponse(
            duration = "duration",
            follows = listOf(followResponse()),
            next = "next",
            prev = "prev",
        )

    fun singleFollowResponse() =
        SingleFollowResponse(duration = "duration", follow = followResponse())

    fun acceptFollowResponse() =
        AcceptFollowResponse(duration = "duration", follow = followResponse())

    fun rejectFollowResponse() =
        RejectFollowResponse(duration = "duration", follow = followResponse())

    fun updateFeedResponse() = UpdateFeedResponse(duration = "duration", feed = feedResponse())

    fun updateFeedMembersResponse() =
        UpdateFeedMembersResponse(
            duration = "duration",
            added = emptyList(),
            removedIds = emptyList(),
            updated = emptyList(),
        )

    fun queryFeedMembersResponse() =
        QueryFeedMembersResponse(
            duration = "duration",
            members = listOf(feedMemberResponse()),
            next = "next",
            prev = "prev",
        )

    fun acceptFeedMemberResponse() =
        AcceptFeedMemberInviteResponse(duration = "duration", member = feedMemberResponse())

    fun rejectFeedMemberResponse() =
        RejectFeedMemberInviteResponse(duration = "duration", member = feedMemberResponse())

    fun followSuggestionsResponse() =
        GetFollowSuggestionsResponse(duration = "duration", suggestions = listOf(feedResponse()))

    fun commentResponse() =
        CommentResponse(
            id = "comment-1",
            createdAt = Date(1000),
            updatedAt = Date(1000),
            text = "Test comment",
            user = userResponse(),
            confidenceScore = 0.9f,
            downvoteCount = 0,
            objectId = "activity-1",
            objectType = "activity",
            reactionCount = 5,
            replyCount = 2,
            score = 10,
            status = "active",
            upvoteCount = 5,
            custom = emptyMap(),
        )

    fun deleteCommentResponse() =
        DeleteCommentResponse(
            duration = "duration",
            activity = activityResponse(),
            comment = commentResponse(),
        )

    fun pollVoteResponseData() =
        PollVoteResponseData(
            id = "vote-1",
            pollId = "poll-1",
            optionId = "option-1",
            createdAt = Date(1000),
            updatedAt = Date(1000),
            user = userResponse(),
        )

    fun <T> defaultPaginationResult(list: List<T>): PaginationResult<T> =
        PaginationResult(
            models = list,
            pagination = PaginationData(next = "next-cursor", previous = null),
        )

    fun activityPinResponse() =
        PinActivityResponse(
            activity = activityResponse(),
            createdAt = Date(1000),
            duration = "duration",
            feed = "user:feed-1",
            userId = "user-1",
        )
}
