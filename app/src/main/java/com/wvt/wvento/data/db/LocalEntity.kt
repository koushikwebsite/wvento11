package com.wvt.wvento.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wvt.wvento.models.Event
import com.wvt.wvento.util.Constants

@Entity(tableName = Constants.Event_TABLE)
data class LocalEntity(
    var event: Event
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}