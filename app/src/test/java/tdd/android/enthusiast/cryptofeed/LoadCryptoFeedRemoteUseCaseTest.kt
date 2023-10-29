package tdd.android.enthusiast.cryptofeed

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import tdd.android.enthusiast.cryptofeed.api.Connectivity
import tdd.android.enthusiast.cryptofeed.api.ConnectivityException
import tdd.android.enthusiast.cryptofeed.api.HttpClient
import tdd.android.enthusiast.cryptofeed.api.LoadCryptoFeedRemoteUseCase

class LoadCryptoFeedRemoteUseCaseTest() {
    private val client = spyk<HttpClient>()
    lateinit var sut: LoadCryptoFeedRemoteUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = LoadCryptoFeedRemoteUseCase(client)
    }

    @Test
    fun testInitDoesNotRequestData() {
        verify(exactly = 0) { // untuk verifikasi kalo client.get kepanggil atau eng
            client.get()
        }
    }

    @Test
    fun testLoadRequestsData() = runBlocking {
        every {
            client.get()
        } returns flowOf()

        sut.load().test {
            awaitComplete()
        }
        verify(exactly = 1) {
            client.get()
        }

        confirmVerified(client)
    }

    @Test
    fun testLoadRequestsDataTwice() = runBlocking {
        every {
            client.get()
        } returns flowOf()

        sut.load().test {
            awaitComplete()
        }
        sut.load().test {
            awaitComplete()
        }
        verify(exactly = 2) {
            client.get()
        }

        confirmVerified(client)
    }

    @Test
    fun testLoadDeliversConnectivityErrorOnClientError() = runBlocking {
        every {
            client.get()
        } returns flowOf(ConnectivityException())

        sut.load().test {
            assertEquals(Connectivity::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        verify(exactly = 1) { client.get() }
        confirmVerified(client)
    }
}