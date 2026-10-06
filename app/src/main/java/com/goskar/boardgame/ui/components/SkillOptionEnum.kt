package com.goskar.boardgame.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.goskar.boardgame.R


enum class SkillOptionEnum(
    @StringRes val label: Int,
    val icon: ImageVector,
    val id: Int
) {
    BEGINNER(R.string.player_skill_beginner, Icons.Default.School, 0),
    INTERMEDIATE(R.string.player_skill_intermediate, Icons.Default.Star, 1),
    MASTER(R.string.player_skill_master, Icons.Default.MilitaryTech, 2);

    companion object {
        fun getLabelFromId(id: Int): Int {
            return SkillOptionEnum.entries.find { it.id == id }?.label ?: BEGINNER.label
        }
    }

}