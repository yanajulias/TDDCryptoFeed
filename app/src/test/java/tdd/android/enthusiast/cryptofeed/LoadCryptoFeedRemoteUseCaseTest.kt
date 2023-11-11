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
import tdd.android.enthusiast.cryptofeed.api.HttpClientResult
import tdd.android.enthusiast.cryptofeed.api.InternalServerError
import tdd.android.enthusiast.cryptofeed.api.InternalServerErrorException
import tdd.android.enthusiast.cryptofeed.api.InvalidData
import tdd.android.enthusiast.cryptofeed.api.InvalidDataException
import tdd.android.enthusiast.cryptofeed.api.LoadCryptoFeedRemoteUseCase
import tdd.android.enthusiast.cryptofeed.api.RemoteCoinInfo
import tdd.android.enthusiast.cryptofeed.api.RemoteCryptoFeedItem
import tdd.android.enthusiast.cryptofeed.api.RemoteDisplay
import tdd.android.enthusiast.cryptofeed.api.RemoteRootCryptoFeed
import tdd.android.enthusiast.cryptofeed.api.RemoteUsd
import tdd.android.enthusiast.cryptofeed.domain.CoinInfo
import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed
import tdd.android.enthusiast.cryptofeed.domain.LoadCryptoFeedResult
import tdd.android.enthusiast.cryptofeed.domain.Raw
import tdd.android.enthusiast.cryptofeed.domain.Usd

class LoadCryptoFeedRemoteUseCaseTest {
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
            receivedHttpClientResult = HttpClientResult.Failure(ConnectivityException()),
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
            receivedHttpClientResult = HttpClientResult.Failure(InvalidDataException()),
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
            receivedHttpClientResult = HttpClientResult.Failure(BadRequestException()),
            expectedResult = BadRequest(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testLoadDeliversInternalServerError() = runBlocking {
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(InternalServerErrorException()),
            expectedResult = InternalServerError(),
            exactly = 1,
            confirmVerified = client
        )
    }

    @Test
    fun testLoadDeliversItemsOn200HttpResponseWithCryptoFeed() {
        val cryptoFeedItemsResponse = listOf(
            RemoteCryptoFeedItem(
                RemoteCoinInfo(
                    "1",
                    "BTC",
                    "Bitcoin",
                    "imageUrl"
                ),
                RemoteDisplay(
                    RemoteUsd(
                        1.0,
                        1F
                    )
                )
            ),
            RemoteCryptoFeedItem(
                RemoteCoinInfo(
                    "2",
                    "BTC 2",
                    "Bitcoin 2",
                    "imageUrl"
                ),
                RemoteDisplay(
                    RemoteUsd(
                        2.0,
                        2F
                    )
                )
            )
        )
        val cryptoFeedItems = listOf(
            CryptoFeed(
                CoinInfo(
                    "1",
                    "BTC",
                    "Bitcoin",
                    "imageUrl"
                ),
                Raw(
                    Usd(
                        1.0,
                        1F
                    )
                )
            ),
            CryptoFeed(
                CoinInfo(
                    "2",
                    "BTC 2",
                    "Bitcoin 2",
                    "imageUrl"
                ),
                Raw(
                    Usd(
                        2.0,
                        2F
                    )
                )
            )
        )
        expect(
            client = client,
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Success(root = RemoteRootCryptoFeed(
                cryptoFeedItemsResponse
            ) ),
            expectedResult = LoadCryptoFeedResult.Success(cryptoFeedItems),
            exactly = 1,
            confirmVerified = client
        )
    }

    private fun expect(
        client: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult: HttpClientResult,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient
    ) = runBlocking {
        every {
            client.get()
        } returns flowOf(receivedHttpClientResult)

        sut.load().test {
            when (val receivedResult = awaitItem()) {
                is LoadCryptoFeedResult.Success -> {
                    assertEquals(
                        expectedResult,
                        receivedResult
                    )
                }

                is LoadCryptoFeedResult.Failure -> {
                    assertEquals(
                        expectedResult::class.java,
                        receivedResult.exception::class.java
                    )
                }
            }

            awaitComplete()
        }

        verify(exactly = exactly) {
            client.get()
        }

        confirmVerified(confirmVerified)
    }
}