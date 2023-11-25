package tdd.android.enthusiast.cryptofeed

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import tdd.android.enthusiast.cryptofeed.api.BadRequestException
import tdd.android.enthusiast.cryptofeed.api.ConnectivityException
import tdd.android.enthusiast.cryptofeed.api.CryptoFeedRetrofitHttpClient
import tdd.android.enthusiast.cryptofeed.api.CryptoFeedService
import tdd.android.enthusiast.cryptofeed.api.HttpClientResult
import tdd.android.enthusiast.cryptofeed.api.InternalServerErrorException
import tdd.android.enthusiast.cryptofeed.api.InvalidDataException
import tdd.android.enthusiast.cryptofeed.api.NotFoundException
import tdd.android.enthusiast.cryptofeed.api.RemoteRootCryptoFeed
import tdd.android.enthusiast.cryptofeed.api.UnexpectedException
import java.io.IOException

class CryptoFeedRetrofitHttpClientTest {
    private val service = mockk<CryptoFeedService>()
    private lateinit var sut: CryptoFeedRetrofitHttpClient

    @Before
    fun setUp() {
        sut = CryptoFeedRetrofitHttpClient(service = service)
    }

    @Test
    fun testGetFailsOnConnectivityError() = runBlocking {
        expect(
            sut = sut,
            expectedResult = ConnectivityException()
        )
    }

    @Test
    fun testGetFailsOn400HttpResponse() {
        expect(
            withStatusCode = 400,
            sut = sut,
            expectedResult = BadRequestException()
        )
    }

    @Test
    fun testGetFailsOn404HttpResponse() {
        expect(
            withStatusCode = 404,
            sut = sut,
            expectedResult = NotFoundException()
        )
    }

    @Test
    fun testGetFailsOn500HttpResponse() {
        expect(
            withStatusCode = 500,
            sut = sut,
            expectedResult = InternalServerErrorException()
        )
    }

    @Test
    fun testGetFailsOn422HttpResponse() {
        expect(
            withStatusCode = 422,
            sut = sut,
            expectedResult = InvalidDataException()
        )
    }

    @Test
    fun testGetFailsOnUnexpectedException() {
        expect(
            sut = sut,
            expectedResult = UnexpectedException()
        )
    }

    private fun expect(
        withStatusCode: Int? = null,
        sut: CryptoFeedRetrofitHttpClient,
        expectedResult: Any
    ) = runBlocking {
        when {
            withStatusCode != null -> {
                val response =
                    Response.error<RemoteRootCryptoFeed>(
                        withStatusCode,
                        ResponseBody.create(null, "")
                    )
                coEvery {
                    service.get()
                } throws HttpException(response)
            }

            expectedResult is ConnectivityException -> {
                coEvery {
                    service.get()
                } throws IOException()
            }

            else -> {
                coEvery {
                    service.get()
                } throws Exception()
            }
        }

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(expectedResult::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }

        confirmVerified(service)
    }
}