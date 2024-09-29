package com.leondeklerk.wheremybike.android

import com.leondeklerk.wheremybike.android.ui.theme.FietsLocatieTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hoc081098.kmp.viewmodel.compose.kmpViewModel
import com.hoc081098.kmp.viewmodel.viewModelFactory
import com.leondeklerk.wheremybike.DriverFactory
import com.leondeklerk.wheremybike.HomeViewModel
import com.leondeklerk.wheremybike.MapsViewModel
import com.leondeklerk.wheremybike.android.ui.HomeScreen
import com.leondeklerk.wheremybike.android.ui.MapsScreen

sealed class Screen(val route: String, val text: Int, val icon: ImageVector) {
    data object Home : Screen("home", R.string.overview, Icons.Filled.Home)
    data object Maps : Screen("maps", R.string.map, Icons.Filled.LocationOn)
}

val screens = listOf(
    Screen.Home,
    Screen.Maps,
)

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FietsLocatieAppBar(
    modifier: Modifier = Modifier,
    showSettings: Boolean = true,
    onSettingsClick: (() -> Unit)
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        actions = {
            AnimatedVisibility(
                visible = showSettings,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
        },

        modifier = modifier,
    )
}

@Composable
fun FietsLocatieBottomAppBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = stringResource(screen.text)) },
                label = { Text(stringResource(screen.text)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

            )
        }
    }
}



@Composable
fun FietsLocatieApp(
    navController: NavHostController = rememberNavController()
) {
    var showSettings by remember { mutableStateOf(true) }
    var settingClickListener by remember { mutableStateOf<(() -> Unit)?>(null) }

    Scaffold(
        topBar = {
            FietsLocatieAppBar(
                showSettings = showSettings,
                onSettingsClick = { settingClickListener?.invoke() })
        },
        bottomBar = {
            FietsLocatieBottomAppBar(navController)
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Home.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )

                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )

                },
            ) {
                val context = LocalContext.current.applicationContext
                showSettings = true
                HomeScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                    { settingClickListener = it },
                    kmpViewModel(
                        factory = viewModelFactory {
                            HomeViewModel(DriverFactory(context))
                        }
                    )
                )

            }
            composable(
                route = Screen.Maps.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )

                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )

                },
            ) {
                val context = LocalContext.current.applicationContext
                showSettings = false
                MapsScreen(
                    modifier = Modifier.fillMaxSize(),
                    kmpViewModel(
                        factory = viewModelFactory {
                            MapsViewModel(DriverFactory(context))
                        }
                    )
                )
            }
        }


    }
}


@Preview("Fiets locatie")
@Composable
fun PreviewFietsLocatieApp() {
    FietsLocatieTheme {
        FietsLocatieApp()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FietsLocatieTheme {
                FietsLocatieApp()
            }
        }
    }
}
