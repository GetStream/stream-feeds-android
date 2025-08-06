package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.PollVoteResponseData
import java.util.Date

/**
 * Data class representing a poll vote.
 *
 * @property answerText The text of the answer, if any.
 * @property createdAt The date and time when the vote was created.
 * @property id Unique identifier for the poll vote.
 * @property isAnswer Whether this vote is the answer, if known.
 * @property optionId The ID of the selected option.
 * @property pollId The ID of the poll.
 * @property updatedAt The date and time when the vote was last updated.
 * @property user The user who voted, if available.
 * @property userId The ID of the user who voted, if available.
 */
public data class PollVoteData(
    val answerText: String?,
    val createdAt: Date,
    val id: String,
    val isAnswer: Boolean?,
    val optionId: String,
    val pollId: String,
    val updatedAt: Date,
    val user: UserData?,
    val userId: String?
)

/**
 * Converts a [PollVoteResponseData] to a [PollVoteData] model.
 */
internal fun PollVoteResponseData.toModel(): PollVoteData = PollVoteData(
    answerText = answerText,
    createdAt = createdAt.toDate(),
    id = id,
    isAnswer = isAnswer,
    optionId = optionId,
    pollId = pollId,
    updatedAt = updatedAt.toDate(),
    user = user?.toModel(),
    userId = userId,
)