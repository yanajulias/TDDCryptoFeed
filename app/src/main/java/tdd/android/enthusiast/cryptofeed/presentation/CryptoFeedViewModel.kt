package tdd.android.enthusiast.cryptofeed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tdd.android.enthusiast.cryptofeed.api.BadRequest
import tdd.android.enthusiast.cryptofeed.api.Connectivity
import tdd.android.enthusiast.cryptofeed.api.InternalServerError
import tdd.android.enthusiast.cryptofeed.api.InvalidData
import tdd.android.enthusiast.cryptofeed.api.NotFound
import tdd.android.enthusiast.cryptofeed.api.Unexpected
import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed
import tdd.android.enthusiast.cryptofeed.domain.LoadCryptoFeedResult
import tdd.android.enthusiast.cryptofeed.domain.LoadCryptoFeedUseCase

data class UiState(
    val isLoading: Boolean = false,
    val cryptoFeed: List<CryptoFeed> = emptyList(),
    val failed: String = ""
)

class CryptoFeedViewModel(private val useCase: LoadCryptoFeedUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            useCase.load().collect { result ->
                _uiState.update {
                    when (result) {
                        is LoadCryptoFeedResult.Success -> TODO()
                        is LoadCryptoFeedResult.Failure -> {
                            it.copy(
                                isLoading = false,
                                failed = when (result.exception) {
                                    is Connectivity -> {
                                        "Tidak ada internet"
                                    }

                                    is InvalidData -> {
                                        "Terjadi kesalahan"
                                    }

                                    is BadRequest -> {
                                        "Permintaan gagal, coba lagi"
                                    }

                                    is NotFound -> {
                                        "Tidak ditemukan, coba lagi"
                                    }

                                    is InternalServerError -> {
                                        "Server sedang dalam perbaikan"
                                    }

                                    is Unexpected -> {
                                        "Terjadi kesalahan, coba lagi"
                                    }

                                    else -> {
                                        "Terjadi kesalahan, coba lagi"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
