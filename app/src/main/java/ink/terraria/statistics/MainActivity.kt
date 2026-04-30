package ink.terraria.statistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import ink.terraria.statistics.ui.navigation.StatisticsNavHost
import ink.terraria.statistics.ui.theme.StatisticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StatisticsTheme {
                val navController = rememberNavController()
                StatisticsNavHost(
                    navController = navController
                )
            }
        }
    }
}

