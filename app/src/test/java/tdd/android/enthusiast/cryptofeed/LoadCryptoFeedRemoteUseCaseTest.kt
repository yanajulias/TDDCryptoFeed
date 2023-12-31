package tdd.android.enthusiast.cryptofeed

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
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
import tdd.android.enthusiast.cryptofeed.api.NotFound
import tdd.android.enthusiast.cryptofeed.api.NotFoundException
import tdd.android.enthusiast.cryptofeed.api.RemoteCryptoFeedItem
import tdd.android.enthusiast.cryptofeed.api.RemoteRootCryptoFeed
import tdd.android.enthusiast.cryptofeed.api.Unexpected
import tdd.android.enthusiast.cryptofeed.api.UnexpectedException
import tdd.android.enthusiast.cryptofeed.domain.CoinInfo
import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed
import tdd.android.enthusiast.cryptofeed.domain.LoadCryptoFeedResult
import tdd.android.enthusiast.cryptofeed.domain.Raw
import tdd.android.enthusiast.cryptofeed.domain.Usd

class LoadCryptoFeedRemoteUseCaseTest {
    private val client = spyk<HttpClient>()
    private lateinit var sut: LoadCryptoFeedRemoteUseCase

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

        confirmVerified(client)
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
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(ConnectivityException()),
            expectedResult = Connectivity(),
            exactly = 1
        )
    }

    @Test
    fun testLoadDeliversInvalidDataError()  {
        expect(
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(InvalidDataException()),
            expectedResult = InvalidData(),
            exactly = 1
        )
    }

    @Test
    fun testLoadDeliversBadRequestError() {
        expect(
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(BadRequestException()),
            expectedResult = BadRequest(),
            exactly = 1
        )
    }

    @Test
    fun  testLoadDeliversNotFoundErrorOnClientError() {
        expect(
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(NotFoundException()),
            expectedResult = NotFound(),
            exactly = 1
        )
    }

    @Test
    fun testLoadDeliversInternalServerError() {
        expect(
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(InternalServerErrorException()),
            expectedResult = InternalServerError(),
            exactly = 1
        )
    }

    @Test
    fun testLoadDeliversUnexpectedError() {
        expect(
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Failure(UnexpectedException()),
            expectedResult = Unexpected(),
            exactly = 1
        )
    }

    @Test
    fun testLoadDeliversItemsOn200HttpResponseWithResponse() {
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
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Success(root = RemoteRootCryptoFeed(
                cryptoFeedResponse
            ) ),
            expectedResult = LoadCryptoFeedResult.Success(cryptoFeedItems),
            exactly = 1
        )
    }

    @Test
    fun testLoadDeliversNotItemsOn200HttpResponseWithEmptyResponse() {
        val cryptoFeedItemsResponse = emptyList<RemoteCryptoFeedItem>()

        val cryptoFeedItems = emptyList<CryptoFeed>()

        expect(
            sut = sut,
            receivedHttpClientResult = HttpClientResult.Success(root = RemoteRootCryptoFeed(
                cryptoFeedItemsResponse
            ) ),
            expectedResult = LoadCryptoFeedResult.Success(cryptoFeedItems),
            exactly = 1
        )
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    private fun expect(
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult: HttpClientResult,
        expectedResult: Any,
        exactly: Int = -1
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

        confirmVerified(client)
    }
}