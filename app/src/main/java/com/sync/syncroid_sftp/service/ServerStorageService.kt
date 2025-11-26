package com.sync.syncroid_sftp.service

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import java.io.InputStream

/**
 * A service class for managing SFTP (SSH File Transfer Protocol) connections and operations.
 *
 * This class provides functionalities to connect to an SFTP server, test the connection,
 * and upload files using an [InputStream]. It uses the JSch library to handle the
 * underlying SFTP communication.
 *
 * @param jsch An instance of [JSch] used for creating SFTP sessions. A default instance is created if not provided.
 */
class ServerStorageService(
    private val jsch: JSch = JSch()
) {

    fun connectToServer(serverStorage: ServerStorage): Boolean {
        var session: Session? = null

        return try {
            // Utilisation de l'instance injectée jsch, pas de nouvelle instanciation
            session = jsch.getSession(serverStorage.username, serverStorage.host, serverStorage.port)
            session.setPassword(serverStorage.password)

            val config = java.util.Properties()
            config["StrictHostKeyChecking"] = "no"
            session.setConfig(config)

            session.connect(10_000)

            val channel = session.openChannel("sftp") as ChannelSftp
            channel.connect(10_000)

            channel.disconnect()
            session.disconnect()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            session?.disconnect()
            false
        }
    }

    /**
     * Uploads a file to the SFTP server using an InputStream.
     */
    fun uploadStream(
        server: ServerStorage,
        inputStream: InputStream,
        remoteDir: String,
        remoteFileName: String
    ): Boolean {
        var session: Session? = null
        var channel: ChannelSftp? = null

        return try {
            session = jsch.getSession(server.username, server.host, server.port)
            session.setPassword(server.password)

            val config = java.util.Properties()
            config["StrictHostKeyChecking"] = "no"
            session.setConfig(config)

            session.connect(10_000)

            channel = session.openChannel("sftp") as ChannelSftp
            channel.connect(10_000)

            try {
                channel.cd(remoteDir)
            } catch (e: Exception) {
                channel.mkdir(remoteDir)
                channel.cd(remoteDir)
            }

            channel.put(inputStream, remoteFileName)

            channel.disconnect()
            session.disconnect()
            true

        } catch (e: Exception) {
            e.printStackTrace()
            channel?.disconnect()
            session?.disconnect()
            false
        }
    }
}
