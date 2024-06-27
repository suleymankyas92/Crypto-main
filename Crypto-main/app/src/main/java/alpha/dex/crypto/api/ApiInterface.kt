package alpha.dex.crypto.api

import alpha.dex.crypto.model.MarketModel
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("data-api/v3/cryptocurrency/listing?start=1&limit=500")
    suspend fun getMarketData(): Response<MarketModel>
}
