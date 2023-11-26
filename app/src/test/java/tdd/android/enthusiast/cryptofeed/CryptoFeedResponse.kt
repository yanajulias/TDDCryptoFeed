package tdd.android.enthusiast.cryptofeed

import tdd.android.enthusiast.cryptofeed.api.RemoteCoinInfo
import tdd.android.enthusiast.cryptofeed.api.RemoteCryptoFeedItem
import tdd.android.enthusiast.cryptofeed.api.RemoteDisplay
import tdd.android.enthusiast.cryptofeed.api.RemoteUsd

val cryptoFeedResponse = listOf(
    RemoteCryptoFeedItem(
        RemoteCoinInfo(
            "1",
            "BTC",
            "Bitcoin",
            "imageUrl"
        ),
        RemoteDisplay(
            RemoteUsd(
                1.0,
                1F
            )
        )
    ),
    RemoteCryptoFeedItem(
        RemoteCoinInfo(
            "2",
            "BTC 2",
            "Bitcoin 2",
            "imageUrl"
        ),
        RemoteDisplay(
            RemoteUsd(
                2.0,
                2F
            )
        )
    )
)