package tdd.android.enthusiast.cryptofeed.api

interface CryptoFeedService {
    suspend fun get(): RemoteRootCryptoFeed
}
