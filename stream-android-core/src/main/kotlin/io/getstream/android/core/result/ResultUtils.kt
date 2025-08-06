package io.getstream.android.core.result

import kotlinx.coroutines.CancellationException

/**
 * Runs a block of code and returns a [Result] containing the outcome.
 *
 * If the block completes successfully, the result is a success with the value returned by the block.
 * If an exception is thrown, it is caught and wrapped in a failure result.
 * Cancellation exceptions are rethrown to allow coroutine cancellation to propagate.
 *
 * @param block The block of code to execute.
 * @return A [Result] containing either the successful value or the exception.
 */
public inline fun <T> runSafely(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
