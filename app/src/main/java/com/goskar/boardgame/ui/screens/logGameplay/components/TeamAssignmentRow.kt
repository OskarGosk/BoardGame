package com.goskar.boardgame.ui.screens.logGameplay.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.AppAvatar
import com.goskar.boardgame.ui.components.AppSelectChip
import com.goskar.boardgame.ui.theme.BoardGameTheme

@Composable
fun TeamAssignmentRow(
    modifier: Modifier = Modifier,
    name: String,
    initials: String,
    team: Int?,
    teamCount: Int,
    onAssign: (Int) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppAvatar(initials = initials, size = 32.dp)
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            (1..teamCount).forEach { number ->
                val description = stringResource(R.string.cd_assign_team, name, number)
                AppSelectChip(
                    text = number.toString(),
                    selected = team == number,
                    onToggle = { onAssign(number) },
                    modifier = Modifier.semantics { contentDescription = description },
                )
            }
        }
    }
}

@Composable
private fun TeamAssignmentRowPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        TeamAssignmentRow(name = "Kamila", initials = "K", team = 2, teamCount = 3, onAssign = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun TeamAssignmentRowLight() = BoardGameTheme(darkTheme = false) { TeamAssignmentRowPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun TeamAssignmentRowDark() = BoardGameTheme(darkTheme = true) { TeamAssignmentRowPreviewContent() }

@Preview(name = "Large font", showBackground = true, backgroundColor = 0xFFF7F9FF, fontScale = 2f)
@Composable
private fun TeamAssignmentRowLargeFont() = BoardGameTheme(darkTheme = false) { TeamAssignmentRowPreviewContent() }
