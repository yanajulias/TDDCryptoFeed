package tdd.android.enthusiast.cryptofeed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadCryptoFeedRemoteUseCase() {
    fun load() {
        HttpClient.instance.getCount = 1
    }
}

class HttpClient private constructor() {
    companion object {
        val instance = HttpClient()
    }

    var getCount = 0
}

class LoadCryptoFeedRemoteUseCaseTest() {
    @Test
    fun testInitDoesNotLoad() {
        val client = HttpClient.instance
        LoadCryptoFeedRemoteUseCase()

        assertTrue(client.getCount == 0) // memastikan kalau tidak di load sama sekali
    }

    @Test
    fun testLoadRequestData() {
        //Given
        val client = HttpClient.instance
        val sut = LoadCryptoFeedRemoteUseCase()

        // When (action)
        sut.load()

        // Then
        assertEquals(1, client.getCount) // cek kalo betul di load 1 kali
    }
}