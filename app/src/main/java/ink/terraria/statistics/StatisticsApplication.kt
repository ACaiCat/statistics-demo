package ink.terraria.statistics

import android.app.Application
import ink.terraria.statistics.data.AppContainer
import ink.terraria.statistics.data.AppDataContainer

class StatisticsApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
    }
}
