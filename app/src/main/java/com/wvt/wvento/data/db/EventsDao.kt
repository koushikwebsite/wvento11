package com.wvt.wvento.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wvt.wvento.models.Results
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocal(localEntity: LocalEntity)

    @Query("SELECT * FROM local_table ORDER BY id DESC")
    fun readLocal(): Flow<List<LocalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExplore(exploreEntity: List<Results>)

    @Query("SELECT * FROM explore_table ORDER BY id DESC")
    fun readExplore(): Flow<List<Results>>

    @Query("SELECT * FROM explore_table WHERE category=:ctg ORDER BY id DESC")
    fun readCategory(ctg: String): Flow<List<Results>>

    @Query("SELECT * FROM explore_table WHERE title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchEvent(query: String): Flow<List<Results>>

}