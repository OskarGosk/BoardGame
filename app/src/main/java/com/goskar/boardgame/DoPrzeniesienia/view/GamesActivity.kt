package OGosk.boardgamebase.view

/*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import OGosk.boardgamebase.view.ui.theme.BoardGameTheme
import OGosk.boardgamebase.viewModel.GameViewModel
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goskar.boardgame.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class GamesActivity : ComponentActivity() {

    private val gameViewModel by viewModel<GameViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            gameViewModel.getAllGame()
        } catch (e: Exception) {
            Log.d ("Game","Bład wczytania listy gier --- $e")
        }

        setContent {
            BoardGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameView()
                }
            }
        }
    }

    @Composable
    fun GameView() {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(id = R.string.board_list),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                if (gameViewModel.gameList.isEmpty()){
                    Text(
                        text = "Empty game list",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    GameViewList()
                }
//                GameViewList()
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(onClick = {
                    val intent = Intent(context, AddEditGames::class.java)
                    context.startActivity(intent)
                    finish()
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_board)
                    )
                }
            }
        }
    }

    @Composable
    fun GameViewList() {
        val context = LocalContext.current

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            /*
            item {
                Card (
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .height(145.dp)
//                        .heightIn(120.dp,200.dp)
                        .padding(5.dp)
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Marvel United",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                        )
                        Text(text = "dodatek")
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Text(text = "Min player")
                            Text(text = "1")
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Text(text = "Max player")
                            Text(text = "3")
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Text(text = "How many played")
                            Text(text = "3")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        )
                        {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add to play")
                            }
                            IconButton(onClick = {
                                val intent = Intent(context, AddEditGames::class.java)
//                                intent.putExtra("edit_game", game)
                                startActivity(intent)
                                finish()

                            }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game")
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete Game")
                            }
                        }
                    }
                }
            }
            item {
                Card (
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.height(140.dp)
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Harry Potter Hogwarts Battle",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                        )
                        Text(text = "dodatke")
                        Text(text = "min player")
                        Text(text = "max player")
                    }
                }
            }
            item {
                Card (
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.height(140.dp)
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "name")
                        Text(text = "dodatke")
                        Text(text = "min player")
                        Text(text = "max player")
                    }
                }
            }

             */
//            /*
                        items(items = gameViewModel.gameList) { game->
                            Card (
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                                modifier = Modifier
                                    .height(200.dp)
                                    .padding(5.dp)
                            ) {
                                Column (
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = game.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                    )
                                    if (game.expansion) Text(text = "expansion") else Text(text = "base")
                                    Row (
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ){
                                        Text(text = "Min player")
                                        Text(text = game.minPlayer)
                                    }
                                    Row (
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ){
                                        Text(text = "Max player")
                                        Text(text = game.maxPlayer)
                                    }
                                    Row (
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ){
                                        Text(text = "How many played")
                                        Text(text = "${game.games}")
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row (
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    )
                                    {
                                        IconButton(onClick = {
                                            val intent = Intent(context, GamePlayActivity::class.java)
                                            intent.putExtra("play_game", game)
                                            startActivity(intent)
                                            finish()
                                        }) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add to play")
                                        }
                                        IconButton(onClick = {
                                            val intent = Intent(context, AddEditGames::class.java)
                                            intent.putExtra("edit_game", game)
                                            startActivity(intent)
                                            finish()
                                        }) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game")
                                        }
                                        IconButton(onClick = { gameViewModel.deleteGame(game) }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Game")
                                        }
                                    }

                                }
                            }
                        }
//                        */
        }
    }

    @Preview
    @Composable
    fun Game_preview() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GameView()
        }
    }
}


 */