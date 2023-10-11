package tdd.android.enthusiast.cryptofeed

import java.util.concurrent.Flow

interface CryptoFeedUseCase {
    fun load(): Flow<CryptoFeedResult>
}


