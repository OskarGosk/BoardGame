package OGosk.boardgamebase.view


/*
class AddEditPlayer : ComponentActivity() {

    val playerViewModel by viewModel<PlayerViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editPlayer : Player? = intent.getSerializableExtra("edit_player") as? Player
        setContent {
            BoardGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlaverAddEditView(editPlayer)
                }
            }
        }
    }

    @Composable
    fun PlaverAddEditView(editPlayer: Player?) {
        val context = LocalContext.current

        var playerName by remember { mutableStateOf(editPlayer?.name ?:"")}
        var playerDescription by remember { mutableStateOf(editPlayer?.description ?:"") }
        
        Column (
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                stringResource(id = R.string.new_player),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = playerName,
                onValueChange = {playerName = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.player_name))
                },
                singleLine = true)

            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                value = playerDescription,
                onValueChange = {playerDescription = it},
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.player_description))
                })
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if (editPlayer == null) {
                        val player = Player(
                            name = playerName,
                            games = editPlayer.games,
                            winRatio = editPlayer.winRatio,
                            description = playerDescription,
                            selected = false
                        )
                        playerViewModel.editPlayer(player)
                    }

                    val intent = Intent(context, PlayersActivity::class.java)
                    context.startActivity(intent)
                    finish()
                },
                modifier = Modifier.fillMaxWidth()) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (editPlayer == null){
                        Text(stringResource(id = R.string.add_player),
                            fontSize = 20.sp)
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_player),
                            modifier = Modifier.size(25.dp)
                        )
                    } else {
                        Text(stringResource(id = R.string.edit_player),
                            fontSize = 20.sp)
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_player),
                            modifier = Modifier.size(25.dp)
                        )
                    }

                }
            }
        }
    }

    @Preview
    @Composable
    fun addPlayerPreview(){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlaverAddEditView(null)
        }
    }
}                     games = 0,
                            winRatio = 0,
                            description = playerDescription,
                            selected = false
                        )
                        playerViewModel.addPlayer(player)
                    } else {
                        val player = editPlayer.copy(
                            name = playerName,



 */

