package tdd.android.enthusiast.cryptofeed.domain

import kotlinx.coroutines.flow.Flow

interface LoadCryptoFeedUseCase {
    fun load(): Flow<LoadCryptoFeedResult>
}


