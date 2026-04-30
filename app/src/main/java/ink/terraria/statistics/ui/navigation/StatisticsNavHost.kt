package ink.terraria.statistics.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ink.terraria.statistics.ui.chart.ChartDestination
import ink.terraria.statistics.ui.chart.ChartScreen
import ink.terraria.statistics.ui.home.HomeDestination
import ink.terraria.statistics.ui.home.HomeScreen

@Composable
fun StatisticsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(
            route = HomeDestination.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            HomeScreen(
                onDrawChartClick = {
                    navController.navigate(ChartDestination.route)
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        composable(
            route = ChartDestination.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            ChartScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
