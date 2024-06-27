package alpha.dex.crypto.ui.activity

import alpha.dex.crypto.R
import alpha.dex.crypto.databinding.ActivityMainBinding
import alpha.dex.crypto.ui.PriceCheckWorker
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val CHANNEL_ID = "price_alert_channel"
    private val CHANNEL_NAME = "Price Alerts"
    private val CHANNEL_DESCRIPTION = "Notifications for price changes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frame)
        navController = navHostFragment!!.findNavController()

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav_menu)
        binding.bottomNavBar.setupWithNavController(popupMenu.menu, navController)

        createNotificationChannel()
        schedulePriceCheckWorker()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun schedulePriceCheckWorker() {
        val workRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
