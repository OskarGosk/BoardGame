package com.goskar.boardgame.ui.games



import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.BoardGameTheme

//class AddEditGames : Screen {
//    @Composable
//    override fun Content() {
//        BoardGameTheme {
//            // A surface container using the 'background' color from the theme
//            Surface(
//                modifier = Modifier.fillMaxSize(),
//                color = MaterialTheme.colorScheme.background
//            ) {
//                GameAddEditContent()
////                observeAddTaskStatus()
//            }
//        }
//    }
//
//}
//
//@Composable
//fun GameAddEditContent() {
//    val context = LocalContext.current
//
//    var gameName by remember { mutableStateOf("Nazwa gry") }
//    var gameMinPlayer by remember { mutableStateOf("Min") }
//    var gameMaxPlayer by remember { mutableStateOf("Max") }
//    var gameBase by remember { mutableStateOf("Dodatek") }
//    var isChecked by remember { mutableStateOf(false) }
//
//    Column (
//        modifier = Modifier.padding(10.dp)
//    ) {
//        Text(
//            stringResource(id = R.string.new_game),
//            modifier = Modifier
//                .fillMaxWidth(),
//            textAlign = TextAlign.Center,
//            fontSize = 25.sp,
//            fontWeight = FontWeight.Bold)
//
//        Spacer(modifier = Modifier.height(25.dp))
//
//        OutlinedTextField(
//            value = gameName,
//            onValueChange = {gameName = it},
//            modifier = Modifier
//                .fillMaxWidth(),
//            label = {
//                Text(stringResource(id = R.string.game_name))
//            },
//            singleLine = true)
//
//        OutlinedTextField(
//            value = gameMinPlayer,
//            onValueChange = {gameMinPlayer = it},
//            modifier = Modifier
//                .fillMaxWidth(),
//            label = {
//                Text(stringResource(id = R.string.min_player))
//            },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//        )
//
//        OutlinedTextField(
//            value = gameMaxPlayer,
//            onValueChange = {gameMaxPlayer = it},
//            modifier = Modifier
//                .fillMaxWidth(),
//            label = {
//                Text(stringResource(id = R.string.max_player))
//            },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//        )
//
//        Row (
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            Checkbox(
//                checked = isChecked,
//                onCheckedChange = { isChecked = !isChecked },
//            )
//            Text(stringResource(id = R.string.expansion))
//        }
//        if(isChecked)
//        {
//            OutlinedTextField(
//                value = gameBase,
//                onValueChange = {gameBase = it},
//                modifier = Modifier
//                    .fillMaxWidth(),
//                label = {
//                    Text(stringResource(id = R.string.base_game))
//                },
//                singleLine = true)
//        }
//
//        Spacer(modifier = Modifier.height(40.dp))
//        Button(
//            onClick = {
//                if (editGames == null) {
//                    val game = Game(
//                        name = gameName,
//                        expansion = isChecked,
//                        baseGame = gameBase,
//                        minPlayer = gameMinPlayer,
//                        maxPlayer = gameMaxPlayer,
//                        games = 0
//                    )
//                    gameViewModel.addGame(game)
//                } else {
//                    val game = editGames.copy(
//                        name = gameName,
//                        expansion = isChecked,
//                        baseGame = gameBase,
//                        minPlayer =  gameMinPlayer,
//                        maxPlayer = gameMaxPlayer,
//                        games = editGames.games
//                    )
//                    gameViewModel.editGame(game)
//                }
//            },
//            modifier = Modifier.fillMaxWidth()) {
//            if(gameViewModel.addEditGameStatus == OperationStatus.LOADING) {
//                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
//            }else {
//                Row (
//                    verticalAlignment = Alignment.CenterVertically
//                ){
//                    if (editGames == null) {
//                        Text(
//                            stringResource(id = R.string.add_board),
//                            fontSize = 20.sp
//                        )
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = stringResource(id = R.string.add_board),
//                            modifier = Modifier.size(25.dp)
//                        )
//                    } else {
//                        Text(
//                            stringResource(id = R.string.edit_game),
//                            fontSize = 20.sp
//                        )
//                        Icon(
//                            imageVector = Icons.Default.Edit,
//                            contentDescription = stringResource(id = R.string.edit_game),
//                            modifier = Modifier.size(25.dp)
//                        )
//
//                    }
//                }
//            }
//
//        }
//    }
//}
//
//@Preview
//@Composable
//fun addPlayerPreview(){
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
////            PlaverAddEditView(null)
//    }
//}

//private fun observeAddTaskStatus() {
//    when (gameViewModel.addEditGameStatus){
//        OperationStatus.SUCCESS -> {
//            val intent = Intent(this, GamesActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        OperationStatus.ERROR -> {
//            Toast.makeText(this,"Connection problem", Toast.LENGTH_LONG).show()
//        }
//        OperationStatus.LOADING, OperationStatus.UNKNOWN -> {}
//    }
//}