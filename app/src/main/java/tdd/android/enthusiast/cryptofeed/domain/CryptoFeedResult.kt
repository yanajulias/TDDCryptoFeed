package tdd.android.enthusiast.cryptofeed.domain

sealed class CryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>) : CryptoFeedResult()
    data class Error(val exception: Exception) : CryptoFeedResult()
}