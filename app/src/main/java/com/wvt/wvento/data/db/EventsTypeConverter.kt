package com.wvt.wvento.data.db

import androidx.room.TypeConverter
import com.wvt.wvento.models.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EventsTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun resultToString(event: Event): String {
        return gson.toJson(event)
    }

    @TypeConverter
    fun stringToResult(data: String): Event {
        val listType = object : TypeToken<Event>() {}.type
        return gson.fromJson(data,listType)
    }


}