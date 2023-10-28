package tdd.android.enthusiast.cryptofeed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
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

    private fun makeSut(): Pair<LoadCryptoFeedRemoteUseCase, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = LoadCryptoFeedRemoteUseCase(client = client)
        return Pair(sut, client)
    }

    private class HttpClientSpy : HttpClient {
        var getCount = 0

        override fun get() {
            getCount += 1
        }
    }
}