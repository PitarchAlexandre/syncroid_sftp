package com.sync.syncroid_sftp.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import com.sync.syncroid_sftp.databinding.ActivityMainBinding
import com.sync.syncroid_sftp.databinding.DialogServerBinding
import com.sync.syncroid_sftp.db.SyncroidDatabase
import com.sync.syncroid_sftp.repository.ServerStorageRepository
import com.sync.syncroid_sftp.service.CryptoService
import com.sync.syncroid_sftp.service.ServerStorageService
import com.sync.syncroid_sftp.ui.adapter.ServerStorageAdapter
import com.sync.syncroid_sftp.viewmodel.serverstorage.ServerStorageViewModel
import com.sync.syncroid_sftp.viewmodel.serverstorage.ServerStorageViewModelFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ServerStorageViewModel
    private lateinit var adapter: ServerStorageAdapter
    private lateinit var binding: ActivityMainBinding
    private val serverStorageService = ServerStorageService()
    private var selectedServerStorage: ServerStorage? = null
    private lateinit var pickFilesLauncher: ActivityResultLauncher<String>
    private var filesToUpload: List<Uri> = emptyList()
    private var cryptoService = CryptoService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setRecyclerView()
        setupClickListeners()
        observeServersStorage()

        pickFilesLauncher =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                if (uris.isNotEmpty()) {
                    onFilesSelected(uris)
                }
            }
    }

    private fun setupViewModel() {
        val database = SyncroidDatabase.getDatabase(this)
        val repository = ServerStorageRepository(database.serverStorageDao())
        val factory = ServerStorageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ServerStorageViewModel::class.java]
    }

    private fun setRecyclerView() {
        adapter = ServerStorageAdapter(
            onItemClick = { serverStorage -> showServerStorageDialog(serverStorage) },
            onLongItemClick = { serverStorage -> showDeleteConfirmationDialog(serverStorage) },
            onDeleteClick = { serverStorage -> showDeleteConfirmationDialog(serverStorage) },
            onTestConnectionClick = { serverStorage -> testConnection(serverStorage) },
            onUploadFilesClick = { serverStorage -> uploadFiles(serverStorage) }
        )
        binding.rvServerStorageList.layoutManager = LinearLayoutManager(this)
        binding.rvServerStorageList.adapter = adapter
    }

    private fun observeServersStorage() {
        viewModel.allServersStorage.observe(this) { list ->
            adapter.updateServerStorageList(list)
            if (list.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvServerStorageList.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvServerStorageList.visibility = View.VISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnNewServerStorage.setOnClickListener { showServerStorageDialog() }
    }

    private fun showDeleteConfirmationDialog(serverStorage: ServerStorage) {
        AlertDialog.Builder(this)
            .setTitle("Delete server")
            .setMessage("Are you sure you want to delete this server?")
            .setPositiveButton("Yes") { _, _ -> viewModel.deleteServerStorage(serverStorage) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun showServerStorageDialog(existingServerStorage: ServerStorage? = null) {
        val dialogBinding = DialogServerBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        existingServerStorage?.let {
            dialogBinding.etServerStorageName.setText(it.name)
            dialogBinding.etServerStorageHost.setText(it.host)
            dialogBinding.etServerStoragePort.setText(it.port.toString())
            dialogBinding.etServerStorageUsername.setText(it.username)

            try {
                val encryptedBytes = android.util.Base64.decode(it.password, android.util.Base64.DEFAULT)
                val decryptedBytes = cryptoService.decrypt(ByteArrayInputStream(encryptedBytes))
                val decryptedPassword = decryptedBytes.toString(Charsets.UTF_8)
                dialogBinding.etServerStoragePassword.setText(decryptedPassword)
            } catch (e: Exception) {
                dialogBinding.etServerStoragePassword.setText("")
            }

            dialogBinding.etRemotePath.setText(it.remotePath)
            dialogBinding.etServerStorageDescription.setText(it.description)
            dialogBinding.btnSave.text = "Update"
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etServerStorageName.text.toString()
            val host = dialogBinding.etServerStorageHost.text.toString()
            val portText = dialogBinding.etServerStoragePort.text.toString()
            val port = portText.toIntOrNull()
            val username = dialogBinding.etServerStorageUsername.text.toString()
            val password = dialogBinding.etServerStoragePassword.text.toString()
            val description = dialogBinding.etServerStorageDescription.text.toString()
            val remotePath = dialogBinding.etRemotePath.text.toString()

            if (name.isEmpty()) {
                dialogBinding.etServerStorageName.error = "Server's name is required"
                return@setOnClickListener
            }
            if (host.isEmpty()) {
                dialogBinding.etServerStorageHost.error = "A host is required"
                return@setOnClickListener
            }
            if (port == null || port !in 1..65535) {
                dialogBinding.etServerStoragePort.error =
                    "The port must be a valid number between 1 and 65535"
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                dialogBinding.etServerStorageUsername.error = "A username is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                dialogBinding.etServerStoragePassword.error = "A password is required"
                return@setOnClickListener
            }
            if (remotePath.isEmpty()) {
                dialogBinding.etRemotePath.error = "A remote path is required"
                return@setOnClickListener
            }

            val encryptedPasswordBase64 = cryptoService.encryptToBase64(password)

            val serverStorage = existingServerStorage?.copy(
                name = name,
                host = host,
                port = port,
                username = username,
                password = encryptedPasswordBase64,
                description = description,
                remotePath = remotePath
            ) ?: ServerStorage(
                name = name,
                host = host,
                port = port,
                username = username,
                password = encryptedPasswordBase64,
                description = description,
                remotePath = remotePath,
                fingerprint = null
            )

            if (existingServerStorage != null) viewModel.updateServerStorage(serverStorage)
            else viewModel.insertServerStorage(serverStorage)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun testConnection(serverStorage: ServerStorage) {
        val decryptedPassword = cryptoService.decryptFromBase64(serverStorage.password)

        val serverWithDecryptedPassword = serverStorage.copy(password = decryptedPassword)

        Thread {
            val result = serverStorageService.connectToServer(serverWithDecryptedPassword)
            runOnUiThread {
                if (result) {
                    Toast.makeText(this, "Connection established!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to connect.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }


    private fun uploadFiles(serverStorage: ServerStorage) {
        selectedServerStorage = serverStorage
        pickFilesLauncher.launch("*/*")
    }

    private fun onFilesSelected(uris: List<Uri>) {
        filesToUpload = uris
        uploadFilesSequentially(0)
    }


    private fun uploadFilesSequentially(index: Int) {
        if (index >= filesToUpload.size) {
            Toast.makeText(this, "All files uploaded!", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = filesToUpload[index]

        Thread {
            try {
                val decryptedPassword = cryptoService.decryptFromBase64(selectedServerStorage!!.password)
                val serverWithDecryptedPassword = selectedServerStorage!!.copy(password = decryptedPassword)

                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val fileName = getFileName(uri)
                    val success = serverStorageService.uploadStream(
                        serverWithDecryptedPassword,
                        inputStream,
                        serverWithDecryptedPassword.remotePath,
                        fileName
                    )
                    runOnUiThread {
                        if (success) {
                            uploadFilesSequentially(index + 1)
                        } else {
                            Toast.makeText(this, "Failed to upload $fileName", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Upload failed with error.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "uploaded_file"
    }


}

