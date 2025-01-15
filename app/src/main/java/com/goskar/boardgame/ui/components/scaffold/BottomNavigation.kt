package com.goskar.boardgame.ui.components.scaffold

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