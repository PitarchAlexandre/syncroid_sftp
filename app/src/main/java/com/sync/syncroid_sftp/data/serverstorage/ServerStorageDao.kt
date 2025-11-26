package com.sync.syncroid_sftp.data.serverstorage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object (DAO) for the [ServerStorage] entity.
 *
 * This interface provides the methods that the rest of the app uses to interact with the
 * `servers_storage` table in the database. It includes methods for querying, inserting,
 * updating, and deleting server storage configurations.
 */
@Dao
interface ServerStorageDao {
    @Query("SELECT * FROM servers_storage")
    fun getAll(): LiveData<List<ServerStorage>>

    @Query("SELECT * FROM servers_storage WHERE id = :serverStorageId")
    suspend fun getById(serverStorageId: Long): ServerStorage

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertServerStorage(serverStorage: ServerStorage): Long

    @Update
    suspend fun updateServerStorage(serverStorage: ServerStorage)

    @Delete
    suspend fun deleteServerStorage(serverStorage: ServerStorage)

    @Query("DELETE FROM servers_storage WHERE id = :id")
    suspend fun deleteServerStorageById(id: Long)

}