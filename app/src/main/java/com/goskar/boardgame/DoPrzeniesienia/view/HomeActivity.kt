package OGosk.boardgamebase.view

import OGosk.boardgamebase.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import OGosk.boardgamebase.view.ui.theme.BoardGameTheme
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoardGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeView()
                }
            }
        }
    }
}


@Composable
fun HomeView () {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        FloatingActionButton(onClick = {
            val intent= Intent(context, PlayersActivity::class.java)
            context.startActivity(intent)
        },
            modifier = Modifier
                .fillMaxWidth()) {
            Text(stringResource(id = R.string.player_list))
        }
        Spacer(modifier = Modifier.height(15.dp))
        FloatingActionButton(onClick = {
            val intent= Intent(context, GamesActivity::class.java)
            context.startActivity(intent)
        },
            modifier = Modifier
                .fillMaxWidth()) {
            Text(stringResource(id = R.string.board_list))
        }
//        Spacer(modifier = Modifier.height(15.dp))
//        FloatingActionButton(onClick = {
//            val intent= Intent(context, GamePlayActivity::class.java)
//            context.startActivity(intent)
//        },
//            modifier = Modifier
//                .fillMaxWidth()) {
//            Text(stringResource(id = R.string.add_gameplay))
//        }
    }



}

@Preview
@Composable
fun HomeActivityPreview() {
    BoardGameTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeView()
        }
    }
}