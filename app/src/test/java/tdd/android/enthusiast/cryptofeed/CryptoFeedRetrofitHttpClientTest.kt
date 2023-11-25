package tdd.android.enthusiast.cryptofeed

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import tdd.android.enthusiast.cryptofeed.api.ConnectivityException
import tdd.android.enthusiast.cryptofeed.api.CryptoFeedRetrofitHttpClient
import tdd.android.enthusiast.cryptofeed.api.HttpClientResult
import java.io.IOException

interface CryptoFeedService {
    suspend fun get()
}

class CryptoFeedRetrofitHttpClientTest {
    private val service = mockk<CryptoFeedService>()
    private lateinit var sut: CryptoFeedRetrofitHttpClient

    @Before
    fun setUp() {
        sut = CryptoFeedRetrofitHttpClient(service = service)
    }

    @Test
    fun testGetFailsOnConnectivityError() = runBlocking {
        coEvery { // karena pake suspend function
            service.get()
        } throws IOException()

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(ConnectivityException()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify {
            service.get()
        }
    }
}