package OGosk.boardgamebase.view

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.unit.dp

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
