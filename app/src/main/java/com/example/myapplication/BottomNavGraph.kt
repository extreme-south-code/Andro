import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.BottomBarScreen
import com.example.myapplication.screens.HomeScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen(maxSelectionCount = 10)
        }
        composable(route = BottomBarScreen.Feed.route) {
            FeedScreen()
        }
        composable(route = BottomBarScreen.Category.route) {
            CategoryScreen()
        }
    }
}