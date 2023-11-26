package tdd.android.enthusiast.cryptofeed

import io.mockk.MockKAnnotations
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Test
import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed

data class UiState(
    val isLoading: Boolean = false,
    val cryptoFeed: List<CryptoFeed> = emptyList(),
    val failed: String = ""
)

class CryptoFeedViewModel{
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}

class CryptoFeedViewModelTest {
    private lateinit var sut : CryptoFeedViewModel

    @Before
    fun setUp(){
        MockKAnnotations.init(this, relaxed = true)
        sut = CryptoFeedViewModel()
    }

    @Test
    fun testInitInitialState(){
        val uiState = sut.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.cryptoFeed.isEmpty())
        assert(uiState.failed.isEmpty())

    }
}