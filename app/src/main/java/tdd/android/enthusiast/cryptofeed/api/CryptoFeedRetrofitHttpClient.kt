package tdd.android.enthusiast.cryptofeed.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class CryptoFeedRetrofitHttpClient(
    private val service: CryptoFeedService
) {
    fun get(): Flow<HttpClientResult> = flow {
        try {
            service.get()
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    emit(HttpClientResult.Failure(ConnectivityException()))
                }

                is HttpException -> {
                    when (exception.code()) {
                        400 -> {
                            emit(HttpClientResult.Failure(BadRequestException()))
                        }

                        404 -> {
                            emit(HttpClientResult.Failure(NotFoundException()))
                        }
                    }

                }
            }
        }
    }
}