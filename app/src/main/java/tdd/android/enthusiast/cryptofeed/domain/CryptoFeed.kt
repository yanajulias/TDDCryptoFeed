package tdd.android.enthusiast.cryptofeed.domain

data class CryptoFeed (
    val coinInfo : CoinInfo,
    val raw: Raw
)

data class CoinInfo (
    val id: String,
    val name: String,
    val fullName: String,
    val image: String
)

data class Raw(
    val usd: Usd
)

data class Usd(
    val price: Double,
    val changePctDay: Float
)