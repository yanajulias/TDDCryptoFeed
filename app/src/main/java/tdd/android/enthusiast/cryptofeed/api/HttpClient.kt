package tdd.android.enthusiast.cryptofeed.api

import kotlinx.coroutines.flow.Flow

sealed class HttpClientResult {
    data class Failure(val exception: Exception) : HttpClientResult()
    data class Success(val root: RemoteRootCryptoFeed) : HttpClientResult()
}

interface HttpClient {
    fun get(): Flow<HttpClientResult>
}
