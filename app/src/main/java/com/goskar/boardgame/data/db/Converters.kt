package com.goskar.boardgame.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeParseException

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToLocalDate(value: String?): LocalDate? {
        return try {
            if (value.isNullOrBlank() || value == "0000-00-00") LocalDate.parse("1900-01-01")
            else LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @TypeConverter
    fun fromLocalDateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromScoresString(value: String?): Map<String, Int>? {
        if (value.isNullOrBlank()) return null
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromScoresMap(map: Map<String, Int>?): String? {
        return map?.let { Gson().toJson(it) }
    }
}