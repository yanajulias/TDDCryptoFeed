package tdd.android.enthusiast.cryptofeed.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tdd.android.enthusiast.cryptofeed.domain.CoinInfo
import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed
import tdd.android.enthusiast.cryptofeed.domain.LoadCryptoFeedResult
import tdd.android.enthusiast.cryptofeed.domain.Raw
import tdd.android.enthusiast.cryptofeed.domain.Usd

// Infrastructure Error (data)
class ConnectivityException : Exception()
class InvalidDataException : Exception()
class BadRequestException : Exception()
class InternalServerErrorException : Exception()
class NotFoundException: Exception()
class UnexpectedException: Exception()

class LoadCryptoFeedRemoteUseCase constructor(
    private val client: HttpClient
) {
    fun load(): Flow<LoadCryptoFeedResult> = flow {
        client.get().collect { result ->
            when (result) {
                is HttpClientResult.Success -> {
                    emit(LoadCryptoFeedResult.Success(result.root.data.toModels()))
                }

                is HttpClientResult.Failure -> {
                    when (result.exception) {
                        is ConnectivityException -> {
                            /*
                             karena yg diterima HttpClientResult,
                             artinya mesti bikin hal yg sama untuk type resultnya didalam domain.
                            */
                            emit(LoadCryptoFeedResult.Failure(Connectivity()))
                        }

                        is InvalidDataException -> {
                            emit(LoadCryptoFeedResult.Failure(InvalidData()))
                        }

                        is BadRequestException -> {
                            emit(LoadCryptoFeedResult.Failure(BadRequest()))
                        }

                        is InternalServerErrorException -> {
                            emit(LoadCryptoFeedResult.Failure(InternalServerError()))
                        }

                        is NotFoundException -> {
                            emit(LoadCryptoFeedResult.Failure(NotFound()))
                        }

                        is UnexpectedException -> {
                            emit(LoadCryptoFeedResult.Failure(Unexpected()))
                        }

                    }
                }

            }
        }
    }
}

private fun List<RemoteCryptoFeedItem>.toModels(): List<CryptoFeed> {
    return map {
        CryptoFeed(
            CoinInfo(
                it.remoteCoinInfo.id,
                it.remoteCoinInfo.name,
                it.remoteCoinInfo.fullName,
                it.remoteCoinInfo.imageUrl
            ),
            Raw(
                Usd(
                    it.remoteRaw.usd.price,
                    it.remoteRaw.usd.changePctDay
                )
            )
        )
    }
}

// Domain Error
class Connectivity : Exception()
class InvalidData : Exception()
class BadRequest : Exception()
class InternalServerError : Exception()
class NotFound: Exception()
class Unexpected : Exception()
