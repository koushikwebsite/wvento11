package com.wvt.wvento.data

import com.wvt.wvento.data.db.EventsDao
import com.wvt.wvento.data.db.LocalEntity
import com.wvt.wvento.models.Results
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val eventsDao: EventsDao
){

    suspend fun insertLocal(exploreEntity: LocalEntity) {
        eventsDao.insertLocal(exploreEntity)
    }

    suspend fun insertExplore(exploreEntity: List<Results>) {
        eventsDao.insertExplore(exploreEntity)
    }

    fun localeDatabase(): Flow<List<LocalEntity>> {
        return eventsDao.readLocal()
    }

    fun exploreDatabase(): Flow<List<Results>> {
        return eventsDao.readExplore()
    }

    fun readCategory(ctg: String): Flow<List<Results>> {
        return eventsDao.readCategory(ctg)
    }

    fun searchEvent(query: String): Flow<List<Results>> {
        return eventsDao.searchEvent(query)
    }

}