package tdd.android.enthusiast.cryptofeed

import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed

sealed class CryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>) : CryptoFeedResult()
    data class Error(val exception: Exception) : CryptoFeedResult()
}