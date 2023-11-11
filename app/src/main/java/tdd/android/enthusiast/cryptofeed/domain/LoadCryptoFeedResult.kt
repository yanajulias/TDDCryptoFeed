package tdd.android.enthusiast.cryptofeed.domain

sealed class LoadCryptoFeedResult {
    data class Success(val cryptoFeedItems: List<CryptoFeed>) : LoadCryptoFeedResult()
    data class Failure(val exception: Exception) : LoadCryptoFeedResult()
    /*
    Di sini ada proses domain mapping error dari infrastructure
    ke domain error
     */
}