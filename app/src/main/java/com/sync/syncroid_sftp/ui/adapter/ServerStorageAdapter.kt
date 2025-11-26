package com.sync.syncroid_sftp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import com.sync.syncroid_sftp.databinding.ItemServerBinding

class ServerStorageAdapter(
    private var serverStorageList: List<ServerStorage> = emptyList(),
    private val onItemClick: (ServerStorage) -> Unit,
    private val onLongItemClick: (ServerStorage) -> Unit,
    private val onDeleteClick: (ServerStorage) -> Unit,
    private val onTestConnectionClick: (ServerStorage) -> Unit,
    private val onUploadFilesClick: (ServerStorage) -> Unit
) : RecyclerView.Adapter<ServerStorageAdapter.ServerStorageViewHolder>() {

    class ServerStorageViewHolder(private val binding: ItemServerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            serverStorage: ServerStorage,
            onItemClick: (ServerStorage) -> Unit,
            onLongItemClick: (ServerStorage) -> Unit,
            onDeleteClick: (ServerStorage) -> Unit,
            onTestConnectionClick: (ServerStorage) -> Unit,
            onUploadFilesClick: (ServerStorage) -> Unit,
            ) {
            binding.tvServerStorageName.text = serverStorage.name
            binding.tvHost.text = serverStorage.host
            binding.tvPort.text = serverStorage.port.toString()
            binding.tvUsername.text = serverStorage.username
            binding.tvRemotePath.text = serverStorage.remotePath
            binding.tvDescription.text = serverStorage.description


            binding.root.setOnLongClickListener {
                onLongItemClick(serverStorage)
                true
            }
            binding.root.setOnClickListener {
                onItemClick(serverStorage)
            }

            binding.btnDeleteServerStorage.setOnClickListener {
                onDeleteClick(serverStorage)
            }
            binding.btnTestConnection.setOnClickListener {
                onTestConnectionClick(serverStorage)
            }


            binding.btnUploadFiles.setOnClickListener { onUploadFilesClick(serverStorage) }


        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServerStorageViewHolder {
        val binding = ItemServerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServerStorageViewHolder(binding)
    }

    override fun getItemCount(): Int = serverStorageList.size

    override fun onBindViewHolder(
        holder: ServerStorageViewHolder,
        position: Int
    ) {
        val serverStorage = serverStorageList[position]
        holder.bind(
            serverStorage,
            onItemClick,
            onLongItemClick,
            onDeleteClick,
            onTestConnectionClick,
            onUploadFilesClick,
        )
    }

    fun updateServerStorageList(newList: List<ServerStorage>) {
        serverStorageList = newList
        notifyDataSetChanged()
    }


}

