package alpha.dex.crypto.model

import alpha.dex.crypto.ui.SavedCoin
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CoinManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("savedList", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCoin(symbol: String, initialPrice: Double) {
        val savedCoins = getSavedCoins().toMutableList()
        savedCoins.add(SavedCoin(symbol, initialPrice))
        storeSavedCoins(savedCoins)
    }

    fun getSavedCoins(): List<SavedCoin> {
        val json = sharedPreferences.getString("savedCoins", "[]")
        val type = object : TypeToken<List<SavedCoin>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun storeSavedCoins(savedCoins: List<SavedCoin>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(savedCoins)
        editor.putString("savedCoins", json)
        editor.apply()
    }
}
