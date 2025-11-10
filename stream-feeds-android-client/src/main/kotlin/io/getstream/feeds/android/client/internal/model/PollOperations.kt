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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.network.models.PollResponseData
import kotlin.math.max

/** Extension function to convert [PollResponseData] to [PollData]. */
internal fun PollResponseData.toModel(): PollData =
    PollData(
        allowAnswers = allowAnswers,
        allowUserSuggestedOptions = allowUserSuggestedOptions,
        answersCount = answersCount,
        createdAt = createdAt,
        createdBy = createdBy?.toModel(),
        createdById = createdById,
        custom = custom,
        description = description,
        enforceUniqueVote = enforceUniqueVote,
        id = id,
        isClosed = isClosed ?: false,
        latestAnswers = latestAnswers.map { it.toModel() },
        latestVotesByOption =
            latestVotesByOption.mapValues { it.value.map { vote -> vote.toModel() } },
        maxVotesAllowed = maxVotesAllowed,
        name = name,
        options = options.map { it.toModel() },
        ownVotes = ownVotes.map { it.toModel() },
        updatedAt = updatedAt,
        voteCount = voteCount,
        voteCountsByOption = voteCountsByOption,
        votingVisibility = votingVisibility,
    )

/**
 * Extension function to update a poll from another poll while preserving own votes because "own"
 * data coming from WS event is not reliable. Optionally, a different ownVotes instance can be
 * provided to be used instead of the current one.
 *
 * @param updatedPoll The poll to update from.
 * @param ownVotes The own votes to preserve. If not provided, the current own votes are preserved.
 * @return A new [PollData] instance with updated information.
 */
internal fun PollData.update(
    updatedPoll: PollData,
    ownVotes: List<PollVoteData> = this.ownVotes,
): PollData = updatedPoll.copy(ownVotes = ownVotes)

/**
 * Extension function to update a poll from another poll while preserving own votes because "own"
 * data coming from WS event is not reliable.
 */
internal fun PollData.update(updated: PollData): PollData = updated.copy(ownVotes = ownVotes)

/**
 * Extension function to add a new option to the poll. This function creates a new [PollData]
 * instance with the new option added to the existing options.
 *
 * @param option The [PollOptionData] to be added to the poll.
 * @return A new [PollData] instance with the added option.
 */
internal fun PollData.addOption(option: PollOptionData): PollData {
    return this.copy(options = this.options.upsert(option, PollOptionData::id))
}

/**
 * Extension function to remove an option from the poll. This function creates a new [PollData]
 * instance with the specified option removed from the existing options.
 *
 * @param optionId The ID of the option to remove.
 * @return A new [PollData] instance with the specified option removed.
 */
internal fun PollData.removeOption(optionId: String): PollData {
    return this.copy(options = this.options.filter { it.id != optionId })
}

/**
 * Extension function to update an existing option in the poll. This function creates a new
 * [PollData] instance with the specified option updated in the existing options.
 *
 * @param option The [PollOptionData] to be updated in the poll.
 * @return A new [PollData] instance with the updated option.
 */
internal fun PollData.updateOption(option: PollOptionData): PollData {
    return this.copy(
        options =
            this.options.map {
                if (it.id == option.id) {
                    option
                } else {
                    it
                }
            }
    )
}

/**
 * Extension function to add or update a vote in the poll.
 *
 * This function creates a new [PollData] instance with the specified vote added or updated in the
 * existing votes. If the vote belongs to the current user, it also updates the user's own votes.
 *
 * @param vote The [PollVoteData] to be added or updated in the poll.
 * @param currentUserId The ID of the current user, used to determine if the vote belongs to them.
 * @return A new [PollData] instance with the specified vote added or updated.
 */
internal fun PollData.upsertVote(vote: PollVoteData, currentUserId: String): PollData {
    // Answers and option votes are mutually exclusive, so if the vote is an answer, we don't need
    // to update the option votes
    if (vote.isAnswer == true) {
        return upsertAnswer(vote = vote, currentUserId = currentUserId)
    }

    val updatedOwnVotes =
        if (vote.userId == currentUserId) {
            ownVotes.upsert(vote, PollVoteData::id)
        } else {
            ownVotes
        }

    val updatedLatestVotesByOption = latestVotesByOption.toMutableMap()
    val updatedVoteCountsByOption = voteCountsByOption.toMutableMap()

    for ((optionId, voteList) in latestVotesByOption) {
        val oldVoteIndex = voteList.indexOfFirst { it.id == vote.id }
        // If the old vote is found, remove it, decrement the vote count, and break the loop
        if (oldVoteIndex >= 0) {
            updatedLatestVotesByOption[optionId] =
                voteList.toMutableList().apply { removeAt(oldVoteIndex) }
            updatedVoteCountsByOption[optionId] =
                updatedVoteCountsByOption[optionId]?.dec()?.coerceAtLeast(0) ?: 0
            break
        }
    }

    // Add the new vote to the appropriate option and increment the vote count
    updatedLatestVotesByOption[vote.optionId] =
        mutableListOf(vote).apply { updatedLatestVotesByOption[vote.optionId]?.let(::addAll) }
    updatedVoteCountsByOption[vote.optionId] = updatedVoteCountsByOption[vote.optionId]?.inc() ?: 1

    return copy(
        ownVotes = updatedOwnVotes,
        latestVotesByOption = updatedLatestVotesByOption,
        voteCountsByOption = updatedVoteCountsByOption,
        voteCount = updatedVoteCountsByOption.values.sum(),
    )
}

/**
 * Extension function to remove a vote from the poll.
 *
 * This function creates a new [PollData] instance with the specified vote removed from the existing
 * votes. If the vote belongs to the current user, it also updates the user's own votes.
 *
 * @param vote The [PollVoteData] to be removed from the poll.
 * @param currentUserId The ID of the current user, used to determine if the vote belongs to them.
 * @return A new [PollData] instance with the specified vote removed.
 */
internal fun PollData.removeVote(vote: PollVoteData, currentUserId: String): PollData {
    // Answers and option votes are mutually exclusive, so if the vote is an answer, we don't need
    // to update the option votes
    if (vote.isAnswer == true) {
        return removeAnswer(vote, currentUserId)
    }

    val updatedOwnVotes =
        if (vote.userId == currentUserId) {
            this.ownVotes.filter { it.id != vote.id }
        } else {
            this.ownVotes
        }
    val votes = latestVotesByOption[vote.optionId].orEmpty()
    val updatedOptionVotes = votes.filter { it.id != vote.id }
    val voteCount = this.voteCountsByOption[vote.optionId] ?: 0
    val updatedOptionVoteCounts =
        if (votes.size != updatedOptionVotes.size) {
            max(0, voteCount - 1)
        } else {
            voteCount
        }
    val updatedLatestVotesByOption =
        latestVotesByOption.toMutableMap().apply { this[vote.optionId] = updatedOptionVotes }
    val updatedVoteCountsByOption =
        voteCountsByOption.toMutableMap().apply { this[vote.optionId] = updatedOptionVoteCounts }
    return this.copy(
        ownVotes = updatedOwnVotes,
        latestVotesByOption = updatedLatestVotesByOption,
        voteCountsByOption = updatedVoteCountsByOption,
        voteCount = updatedVoteCountsByOption.values.sum(),
    )
}

private fun PollData.upsertAnswer(vote: PollVoteData, currentUserId: String): PollData {
    val updatedOwnVotes =
        if (vote.userId == currentUserId) {
            ownVotes.upsert(vote, PollVoteData::id)
        } else {
            ownVotes
        }

    val updatedAnswers = latestAnswers.upsert(vote, PollVoteData::id)
    val updatedAnswerCount =
        if (updatedAnswers.size != latestAnswers.size) {
            answersCount + 1
        } else {
            answersCount
        }

    return copy(
        ownVotes = updatedOwnVotes,
        latestAnswers = updatedAnswers,
        answersCount = updatedAnswerCount,
    )
}

private fun PollData.removeAnswer(vote: PollVoteData, currentUserId: String): PollData {
    val updatedOwnVotes =
        if (vote.userId == currentUserId) {
            ownVotes.filter { it.id != vote.id }
        } else {
            ownVotes
        }

    val updatedAnswers = latestAnswers.filter { it.id != vote.id }
    val updatedAnswerCount =
        if (latestAnswers.size != updatedAnswers.size) {
            (answersCount - 1).coerceAtLeast(0)
        } else {
            answersCount
        }

    return copy(
        ownVotes = updatedOwnVotes,
        latestAnswers = updatedAnswers,
        answersCount = updatedAnswerCount,
    )
}
