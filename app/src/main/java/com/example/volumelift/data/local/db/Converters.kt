package com.example.volumelift.data.local.db

import androidx.room.TypeConverter
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.data.local.entity.SetType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = MuscleGroup.valueOf(value)

    @TypeConverter
    fun fromSetType(value: SetType): String = value.name

    @TypeConverter
    fun toSetType(value: String): SetType = SetType.valueOf(value)

    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>): String =
        gson.toJson(value.map { it.name })

    @TypeConverter
    fun toMuscleGroupList(value: String): List<MuscleGroup> {
        val type = object : TypeToken<List<String>>() {}.type
        val names: List<String> = gson.fromJson(value, type)
        return names.map { MuscleGroup.valueOf(it) }
    }

    @TypeConverter
    fun fromLongList(value: List<Long>): String = gson.toJson(value)

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, type)
    }
}
