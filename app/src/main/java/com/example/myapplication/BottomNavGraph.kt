import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.BottomBarScreen
import com.example.myapplication.screens.HomeScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Feed.route) {
            FeedScreen()
        }
        composable(route = BottomBarScreen.Category.route) {
            CategoryScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavGraphPreview() {
    val navController = rememberNavController()
    BottomNavGraph(navController = navController)
}