package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.core.generated.models.PollResponseData
import java.util.Date
import kotlin.math.max

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

/**
 * Extension function to add a new option to the poll.
 * This function creates a new [PollData] instance with the new option added to the existing
 * options.
 *
 * @param option The [PollOptionData] to be added to the poll.
 * @return A new [PollData] instance with the added option.
 */
internal fun PollData.addOption(option: PollOptionData): PollData {
    return this.copy(
        options = this.options.upsert(option, PollOptionData::id)
    )
}

/**
 * Extension function to remove an option from the poll.
 * This function creates a new [PollData] instance with the specified option removed from the
 * existing options.
 *
 * @param optionId The ID of the option to remove.
 * @return A new [PollData] instance with the specified option removed.
 */
internal fun PollData.removeOption(optionId: String): PollData {
    return this.copy(
        options = this.options.filter { it.id != optionId }
    )
}

/**
 * Extension function to update an existing option in the poll.
 * This function creates a new [PollData] instance with the specified option updated in the
 * existing options.
 *
 * @param option The [PollOptionData] to be updated in the poll.
 * @return A new [PollData] instance with the updated option.
 */
internal fun PollData.updateOption(option: PollOptionData): PollData {
    return this.copy(
        options = this.options.map {
            if (it.id == option.id) {
                option
            } else {
                it
            }
        }
    )
}

internal fun PollData.castVote(vote: PollVoteData, currentUserId: String): PollData {
    val updatedOwnVotes = if (vote.userId == currentUserId) {
        this.ownVotes.upsert(vote, PollVoteData::id)
    } else {
        this.ownVotes
    }
    val votes = latestVotesByOption[vote.optionId].orEmpty()
    val updatedOptionVotes = votes.upsert(vote, PollVoteData::id)
    val voteCount = this.voteCountsByOption[vote.optionId] ?: 0
    val updatedOptionVoteCounts = if (votes.size != updatedOptionVotes.size) {
        voteCount + 1
    } else {
        voteCount
    }
    val updatedLatestVotesByOption = latestVotesByOption.toMutableMap().apply {
        this[vote.optionId] = updatedOptionVotes
    }
    val updatedVoteCountsByOption = voteCountsByOption.toMutableMap().apply {
        this[vote.optionId] = updatedOptionVoteCounts
    }
    return this.copy(
        ownVotes = updatedOwnVotes,
        latestVotesByOption = updatedLatestVotesByOption,
        voteCountsByOption = updatedVoteCountsByOption,
        voteCount = updatedVoteCountsByOption.values.sum(),
    )
}

/**
 * Extension function to remove a vote from the poll.
 *
 * This function creates a new [PollData] instance with the specified vote removed from the
 * existing votes. If the vote belongs to the current user, it also updates the user's own votes.
 *
 * @param vote The [PollVoteData] to be removed from the poll.
 * @param currentUserId The ID of the current user, used to determine if the vote belongs to them.
 * @return A new [PollData] instance with the specified vote removed.
 */
internal fun PollData.removeVote(vote: PollVoteData, currentUserId: String): PollData {
    val updatedOwnVotes = if (vote.userId == currentUserId) {
        this.ownVotes.filter { it.id != vote.id }
    } else {
        this.ownVotes
    }
    val votes = latestVotesByOption[vote.optionId].orEmpty()
    val updatedOptionVotes = votes.filter { it.id != vote.id }
    val voteCount = this.voteCountsByOption[vote.optionId] ?: 0
    val updatedOptionVoteCounts = if (votes.size != updatedOptionVotes.size) {
        max(0, voteCount - 1)
    } else {
        voteCount
    }
    val updatedLatestVotesByOption = latestVotesByOption.toMutableMap().apply {
        this[vote.optionId] = updatedOptionVotes
    }
    val updatedVoteCountsByOption = voteCountsByOption.toMutableMap().apply {
        this[vote.optionId] = updatedOptionVoteCounts
    }
    return this.copy(
        ownVotes = updatedOwnVotes,
        latestVotesByOption = updatedLatestVotesByOption,
        voteCountsByOption = updatedVoteCountsByOption,
        voteCount = updatedVoteCountsByOption.values.sum(),
    )
}


/**
 * Extension function to convert [PollResponseData] to [PollData].
 */
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

