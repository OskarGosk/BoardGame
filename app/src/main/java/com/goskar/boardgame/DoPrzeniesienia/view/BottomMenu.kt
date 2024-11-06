package OGosk.boardgamebase.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.BoardGameTheme

class BottomMenu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoardGameTheme {


            }
        }
    }
}

@Composable
fun BottomMenuChosser() {

    Box(
        modifier = Modifier
            .height(60.dp)

    ){
        Row {

        }
    }
}
