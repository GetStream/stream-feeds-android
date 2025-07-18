package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.PollResponseData
import java.util.Date

/**
 * Data class representing a poll.
 *
 * @property allowAnswers Whether answers are allowed.
 * @property allowUserSuggestedOptions Whether user-suggested options are allowed.
 * @property answersCount The number of answers.
 * @property createdAt The date and time when the poll was created.
 * @property createdBy The user who created the poll, if available.
 * @property createdById The ID of the user who created the poll.
 * @property custom Custom data as a map.
 * @property description The description of the poll.
 * @property enforceUniqueVote Whether unique voting is enforced.
 * @property id Unique identifier for the poll.
 * @property isClosed Whether the poll is closed.
 * @property latestAnswers The latest answers to the poll.
 * @property latestVotesByOption The latest votes by option.
 * @property maxVotesAllowed The maximum number of votes allowed, if any.
 * @property name The name of the poll.
 * @property options The options for the poll.
 * @property ownVotes The votes made by the current user.
 * @property updatedAt The date and time when the poll was last updated.
 * @property voteCount The total number of votes.
 * @property voteCountsByOption The number of votes by option.
 * @property votingVisibility The visibility of voting.
 */
public data class PollData(
    val allowAnswers: Boolean,
    val allowUserSuggestedOptions: Boolean,
    val answersCount: Int,
    val createdAt: Date,
    val createdBy: UserData?,
    val createdById: String,
    val custom: Map<String, Any?>,
    val description: String,
    val enforceUniqueVote: Boolean,
    val id: String,
    val isClosed: Boolean,
    val latestAnswers: List<PollVoteData>,
    val latestVotesByOption: Map<String, List<PollVoteData>>,
    val maxVotesAllowed: Int?,
    val name: String,
    val options: List<PollOptionData>,
    val ownVotes: List<PollVoteData>,
    val updatedAt: Date,
    val voteCount: Int,
    val voteCountsByOption: Map<String, Int>,
    val votingVisibility: String
)

internal fun PollResponseData.toModel(): PollData = PollData(
    allowAnswers = allowAnswers,
    allowUserSuggestedOptions = allowUserSuggestedOptions,
    answersCount = answersCount,
    createdAt = createdAt.toDate(),
    createdBy = createdBy?.toModel(),
    createdById = createdById,
    custom = custom,
    description = description,
    enforceUniqueVote = enforceUniqueVote,
    id = id,
    isClosed = isClosed ?: false,
    latestAnswers = latestAnswers.map { it.toModel() },
    latestVotesByOption = latestVotesByOption.mapValues { it.value.map { vote -> vote.toModel() } },
    maxVotesAllowed = maxVotesAllowed,
    name = name,
    options = options.map { it.toModel() },
    ownVotes = ownVotes.map { it.toModel() },
    updatedAt = updatedAt.toDate(),
    voteCount = voteCount,
    voteCountsByOption = voteCountsByOption,
    votingVisibility = votingVisibility
)

