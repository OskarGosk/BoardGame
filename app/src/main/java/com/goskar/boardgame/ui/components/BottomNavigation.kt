package pl.ecp.app.ui.components.scaffold

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator

//@Composable
//fun EcpBottomNavigation(
//    selectedScreen: Int?
//) {
//    val navigator = LocalNavigator.current
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(White),
//        horizontalArrangement = Arrangement.SpaceAround
//    ) {
//        BottomBarElements.entries.forEach { elements ->
//            Column {
//                Divider(
//                    modifier = Modifier
//                        .width(72.dp)
//                        .padding(bottom = 4.dp),
//                    color = if (selectedScreen == elements.title) Black else White
//                )
//                Box (
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier.width(72.dp)
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(vertical = 4.dp)
//                            .clickable {
//                                if (selectedScreen == elements.title && navigator?.lastItem is OffersScreen) {
//                                    val offersScreen: OffersScreen =
//                                        navigator.lastItem as OffersScreen
//                                    offersScreen.clear()
//                                } else {
//                                    elements.navigationScreen?.let {
//                                        navigator?.replace(it)
//                                    }
//                                }
//                            },
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Image(
//                            painter =
//                            if (selectedScreen == elements.title)
//                                painterResource(elements.itemSelected)
//                            else
//                                painterResource(
//                                    elements.itemNotSelected
//                                ),
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Text(
//                            text = stringResource(elements.title),
//                            style = Lato12line16
//                        )
//                    }
//                }
//            }
//        }
//    }
//}