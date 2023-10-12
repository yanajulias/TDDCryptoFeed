package tdd.android.enthusiast.cryptofeed.domain

import kotlinx.coroutines.flow.Flow

interface CryptoFeedUseCase {
    fun load(): Flow<CryptoFeedResult>
}


