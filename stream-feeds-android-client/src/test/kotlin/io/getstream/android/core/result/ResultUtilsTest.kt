package io.getstream.android.core.result

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

internal class ResultUtilsTest {
    @Test
    fun `runSafely, when no error occurs, then return success`() {
        val result = runSafely { "Success" }

        assertEquals(Result.success("Success"), result)
    }

    @Test
    fun `runSafely, when an exception occurs, then return failure`() {
        val exception = RuntimeException("Test Exception")
        val result = runSafely { throw exception }

        assertEquals(Result.failure<String>(exception), result)
    }

    @Test(expected = CancellationException::class)
    fun `runSafely, when a CancellationException occurs, then rethrow it`() {
        runSafely { throw CancellationException("Cancellation") }
    }
}
