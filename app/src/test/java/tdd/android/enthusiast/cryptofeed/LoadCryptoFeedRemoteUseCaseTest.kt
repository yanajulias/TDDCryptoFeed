package tdd.android.enthusiast.cryptofeed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tdd.android.enthusiast.cryptofeed.api.Connectivity
import tdd.android.enthusiast.cryptofeed.api.HttpClient
import tdd.android.enthusiast.cryptofeed.api.LoadCryptoFeedRemoteUseCase

class LoadCryptoFeedRemoteUseCaseTest() {
    @Test
    fun testInitDoesNotRequestData() {
        val (_, client) = makeSut()

        assertTrue(client.getCount == 0) // memastikan kalau tidak di load sama sekali
    }

    @Test
    fun testLoadRequestsData() {
        //Given
        val (sut, client) = makeSut()

        // When (action)
        sut.load()

        // Then
        assertEquals(1, client.getCount) // cek kalo betul di load 1 kali
    }

    @Test
    fun testLoadRequestsDataTwice() {
        //Given
        val (sut, client) = makeSut()

        // When (action)
        sut.load()
        sut.load()

        // Then
        assertEquals(2, client.getCount) // cek kalo betul di load 1 kali
    }

    @Test
    fun testLoadDeliversErrorOnClientError() = runBlocking {
        val (sut, client) = makeSut()

        client.error = Exception("Test")

        val capturedError = arrayListOf<Exception>()
        sut.load().collect { error ->
            capturedError.add(error)
        }

        assertEquals(listOf(Connectivity::class.java), capturedError.map { it::class.java })
    }

    private fun makeSut(): Pair<LoadCryptoFeedRemoteUseCase, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(client = client)
        return Pair(sut, client)
    }

    private class HttpClientSpy : HttpClient {
        var getCount = 0
        var error: Exception? = null

        override fun get(): Flow<Exception> = flow {
            if (error != null) {
                emit(error ?: Exception())
            }
            getCount += 1
        }
    }
}