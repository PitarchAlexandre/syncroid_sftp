package com.sync.syncroid_sftp.viewmodel.serverstorage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sync.syncroid_sftp.repository.ServerStorageRepository

@Suppress("UNCHECKED_CAST")
class ServerStorageViewModelFactory(private val repository: ServerStorageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServerStorageViewModel::class.java)) {
            return ServerStorageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}