package com.wvt.wvento.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wvt.wvento.models.Results

@Database(
    entities = [Results::class, LocalEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EventsTypeConverter::class)
abstract class EventsDatabase: RoomDatabase() {

    abstract fun eventsDao(): EventsDao

}