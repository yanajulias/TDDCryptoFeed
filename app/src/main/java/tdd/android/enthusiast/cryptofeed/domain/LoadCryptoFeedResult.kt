package tdd.android.enthusiast.cryptofeed.domain

sealed class LoadCryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>) : LoadCryptoFeedResult()
    data class Error(val exception: Exception) : LoadCryptoFeedResult()
}