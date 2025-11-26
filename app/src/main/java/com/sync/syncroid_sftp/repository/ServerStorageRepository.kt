package com.sync.syncroid_sftp.repository

import androidx.lifecycle.LiveData
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import com.sync.syncroid_sftp.data.serverstorage.ServerStorageDao

/**
 * Repository class for managing ServerStorage data.
 *
 * @param serverStorageDao The Data Access Object for the ServerStorage entity.
 */
class ServerStorageRepository(private val serverStorageDao: ServerStorageDao) {

    val allServerStorages: LiveData<List<ServerStorage>> = serverStorageDao.getAll()

    suspend fun insertServerStorage(serverStorage: ServerStorage): Long {
        return serverStorageDao.insertServerStorage(serverStorage)
    }

    suspend fun updateServerStorage(serverStorage: ServerStorage) {
        serverStorageDao.updateServerStorage(serverStorage)
    }

    suspend fun deleteServerStorage(serverStorage: ServerStorage) {
        serverStorageDao.deleteServerStorage(serverStorage)
    }

    suspend fun getServerStorageById(serverStorageId: Long): ServerStorage {
        return serverStorageDao.getById(serverStorageId)
    }

}