package com.sync.syncroid_sftp.viewmodel.serverstorage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import com.sync.syncroid_sftp.repository.ServerStorageRepository
import kotlinx.coroutines.launch

class ServerStorageViewModel(private val repository: ServerStorageRepository) : ViewModel() {

    val allServersStorage = repository.allServerStorages

    fun insertServerStorage(serverStorage: ServerStorage) = viewModelScope.launch {
        repository.insertServerStorage(serverStorage)
    }

    fun updateServerStorage(serverStorage: ServerStorage) = viewModelScope.launch {
        repository.updateServerStorage(serverStorage)
    }

    fun deleteServerStorage(serverStorage: ServerStorage) = viewModelScope.launch {
        repository.deleteServerStorage(serverStorage)
    }

    fun getServerStorageById(serverStorageId: Long, onResult:(ServerStorage)-> Unit) = viewModelScope.launch {
        val serverStorage = repository.getServerStorageById(serverStorageId)
        onResult(serverStorage)
    }
}