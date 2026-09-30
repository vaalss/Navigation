package plat.lab7.hernandez_maldonado.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import plat.lab7.hernandez_maldonado.core.ui.theme.RickAndMortyAppTheme

enum class MainTab {
    Characters,
    Locations,
    Profile
}

@Composable
fun BottomNavigationBar(
    selectedTab: MainTab,
    onTabClick: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        MainTab.entries.forEach { tab ->
            val icon = when (tab) {
                MainTab.Characters -> Icons.Filled.People
                MainTab.Locations -> Icons.Filled.Public
                MainTab.Profile -> Icons.Filled.Person
            }

            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = {
                    onTabClick(tab)
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = tab.name)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor =
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor =
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor =
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    RickAndMortyAppTheme {
        BottomNavigationBar(
            selectedTab = MainTab.Characters,
            onTabClick = {}
        )
    }
}