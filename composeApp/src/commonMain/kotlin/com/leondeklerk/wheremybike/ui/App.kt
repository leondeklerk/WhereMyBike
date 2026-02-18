package com.leondeklerk.wheremybike.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leondeklerk.wheremybike.resources.Res
import com.leondeklerk.wheremybike.resources.app_name
import com.leondeklerk.wheremybike.resources.map
import com.leondeklerk.wheremybike.resources.overview
import com.leondeklerk.wheremybike.resources.settings
import com.leondeklerk.wheremybike.ui.screens.home.HomeScreen
import com.leondeklerk.wheremybike.ui.screens.maps.MapsScreen
import com.leondeklerk.wheremybike.ui.theme.WhereMyBikeTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object Routes {
    const val HOME = "home"
    const val MAPS = "maps"
}

data class BottomNavItem(
    val route: String,
    val titleRes: StringResource,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, Res.string.overview, Icons.Filled.Home),
    BottomNavItem(Routes.MAPS, Res.string.map, Icons.Filled.LocationOn)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhereMyBikeAppBar(
    modifier: Modifier = Modifier,
    showSettings: Boolean = true,
    isDebug: Boolean = false,
    onSettingsClick: () -> Unit
) {
    val appName = stringResource(Res.string.app_name)
    val title = if (isDebug) "$appName (D)" else appName
    TopAppBar(
        title = { Text(title) },
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
                        contentDescription = stringResource(Res.string.settings)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun WhereMyBikeBottomBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                label = { Text(stringResource(item.titleRes)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Routes.HOME) {
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
fun WhereMyBikeApp(
    navController: NavHostController = rememberNavController(),
    isDebug: Boolean = false
) {
    var showSettings by remember { mutableStateOf(true) }
    var settingClickListener by remember { mutableStateOf<(() -> Unit)?>(null) }

    WhereMyBikeTheme {
        Scaffold(
            topBar = {
                WhereMyBikeAppBar(
                    showSettings = showSettings,
                    isDebug = isDebug,
                    onSettingsClick = { settingClickListener?.invoke() }
                )
            },
            bottomBar = {
                WhereMyBikeBottomBar(navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = Routes.HOME,
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
                    }
                ) {
                    showSettings = true
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        setSettingClickListener = { settingClickListener = it }
                    )
                }

                composable(
                    route = Routes.MAPS,
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
                    }
                ) {
                    showSettings = false
                    MapsScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}




