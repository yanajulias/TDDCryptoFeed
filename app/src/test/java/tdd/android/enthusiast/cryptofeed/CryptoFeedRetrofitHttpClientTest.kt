package tdd.android.enthusiast.cryptofeed

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
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
import tdd.android.enthusiast.cryptofeed.api.NotFoundException
import tdd.android.enthusiast.cryptofeed.api.RemoteRootCryptoFeed
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
        coEvery { // karena pake suspend function
            service.get()
        } throws IOException()

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(ConnectivityException()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }

    @Test
    fun testGetFailsOn400HttpResponse() = runBlocking {
        val response = Response.error<RemoteRootCryptoFeed>(400, ResponseBody.create(null, ""))

        coEvery { // karena pake suspend function
            service.get()
        } throws HttpException(response)

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(BadRequestException()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }

    @Test
    fun testGetFailsOn404HttpResponse() = runBlocking {
        val response = Response.error<RemoteRootCryptoFeed>(404, ResponseBody.create(null, ""))

        coEvery { // karena pake suspend function
            service.get()
        } throws HttpException(response)

        sut.get().test {
            val receivedValue = awaitItem() as HttpClientResult.Failure
            assertEquals(NotFoundException()::class.java, receivedValue.exception::class.java)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            service.get()
        }
    }
}