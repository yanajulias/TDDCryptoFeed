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
import tdd.android.enthusiast.cryptofeed.api.BadRequest
import tdd.android.enthusiast.cryptofeed.api.BadRequestException
import tdd.android.enthusiast.cryptofeed.api.Connectivity
import tdd.android.enthusiast.cryptofeed.api.ConnectivityException
import tdd.android.enthusiast.cryptofeed.api.HttpClient
import tdd.android.enthusiast.cryptofeed.api.InvalidData
import tdd.android.enthusiast.cryptofeed.api.InvalidDataException
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
    fun testLoadDeliversConnectivityErrorOnClientError() {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = ConnectivityException(),
            expectedResult = Connectivity(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testLoadDeliversInvalidDataError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = InvalidDataException(),
            expectedResult = InvalidData(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testLoadDeliversBadRequestError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = BadRequestException(),
            expectedResult = BadRequest(),
            exactly = 1,
            confirmVerified = client
        )
    }

    private fun expect(
        client: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult: Exception,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient
    ) = runBlocking {
        every {
            client.get()
        } returns flowOf(receivedHttpClientResult)

        sut.load().test {
            assertEquals(expectedResult::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        verify(exactly = exactly) {
            client.get()
        }

        confirmVerified(confirmVerified)
    }
}