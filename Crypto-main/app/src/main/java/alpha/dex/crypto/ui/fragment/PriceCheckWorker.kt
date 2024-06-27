package alpha.dex.crypto.ui

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import alpha.dex.crypto.R
import alpha.dex.crypto.api.ApiInterface
import alpha.dex.crypto.api.ApiUtilities
import alpha.dex.crypto.model.MarketModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class PriceCheckWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val savedCoins = getSavedCoins()
        val currentPrices = fetchCurrentPrices(savedCoins)

        for (coin in savedCoins) {
            val currentPrice = currentPrices[coin.symbol] ?: continue
            if (hasIncreasedByFivePercent(coin.initialPrice, currentPrice)) {
                sendNotification(coin)
            }
        }

        return Result.success()
    }

    private fun getSavedCoins(): List<SavedCoin> {
        val sharedPreferences = applicationContext.getSharedPreferences("savedList", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("savedCoins", "[]")
        val type = object : TypeToken<List<SavedCoin>>() {}.type
        return Gson().fromJson(json, type)
    }

    private suspend fun fetchCurrentPrices(savedCoins: List<SavedCoin>): Map<String, Double> {
        return withContext(Dispatchers.IO) {
            val api = ApiUtilities.getInstance().create(ApiInterface::class.java)
            val response: Response<MarketModel> = try {
                api.getMarketData()
            } catch (e: HttpException) {
                null
            } ?: return@withContext emptyMap()

            val currentPrices = mutableMapOf<String, Double>()

            if (response.isSuccessful) {
                val coins = response.body()?.data?.cryptoCurrencyList ?: emptyList()
                for (coin in coins) {
                    currentPrices[coin.symbol] = coin.quotes[0].price
                }
            }
            currentPrices
        }
    }

    private fun hasIncreasedByFivePercent(initialPrice: Double, currentPrice: Double): Boolean {
        val percentageChange = ((currentPrice - initialPrice) / initialPrice) * 100
        return percentageChange >= 5
    }

    private fun sendNotification(coin: SavedCoin) {
        val builder = NotificationCompat.Builder(applicationContext, "price_alert_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Price Alert")
            .setContentText("${coin.symbol} has increased by 5% or more!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(coin.symbol.hashCode(), builder.build())
        }
    }
}

data class SavedCoin(
    val symbol: String,
    val initialPrice: Double
)
