package tdd.android.enthusiast.cryptofeed

import tdd.android.enthusiast.cryptofeed.domain.CoinInfo
import tdd.android.enthusiast.cryptofeed.domain.CryptoFeed
import tdd.android.enthusiast.cryptofeed.domain.Raw
import tdd.android.enthusiast.cryptofeed.domain.Usd

val cryptoFeed = listOf(
    CryptoFeed(
        CoinInfo(
            "1",
            "BTC",
            "Bitcoin",
            "imageUrl"
        ),
        Raw(
            Usd(
                1.0,
                1F
            )
        )
    ),
    CryptoFeed(
        CoinInfo(
            "2",
            "BTC 2",
            "Bitcoin 2",
            "imageUrl"
        ),
        Raw(
            Usd(
                2.0,
                2F
            )
        )
    )
)