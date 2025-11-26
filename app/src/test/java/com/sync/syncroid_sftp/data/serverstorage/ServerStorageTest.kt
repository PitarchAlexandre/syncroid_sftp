package com.sync.syncroid_sftp.data.serverstorage

import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test
import java.util.Date

class ServerStorageTest {

    val serverStorageTest = ServerStorage(
        name = "test",
        host = "192.168.1.145",
        port = 22,
        username = "username_test",
        password = "PaSSw0rd",
        remotePath = "/home/admin",
        description = "This server is configured as a multi-purpose home hub designed to centralize " +
                "media, storage, and personal services. Its primary role is to host a private Plex " +
                "Media setup, allowing movies, shows, and music to be organized cleanly and streamed" +
                " to devices across the home or remotely. The server’s storage structure is " +
                "optimized for fast indexing, ensuring smooth playback and easy library expansion.\n" +
                "Beyond media, the server is used for personal data management, including automated " +
                "photo and video backups from phones and computers. Files are neatly sorted, " +
                "versioned when needed, and kept available for quick retrieval. The system also" +
                " supports small, self-hosted utilities such as shared document folders, lightweight" +
                " note archives, or sync spaces for personal projects.\n" +
                "\n" +
                "Overall, this server acts as a reliable, always-on companion for everyday digital" +
                " needs. It keeps media accessible, photos safe, and private tools running " +
                "consistently, making it a cornerstone of the user’s personal home ecosystem.",
        fingerprint = null,
        createdAt = Date().time,
    )

    @Test
    fun mustReturnTheServerStorageTest_withNoErros() {

        TestCase.assertNotNull(serverStorageTest.toString())
        TestCase.assertEquals("test", serverStorageTest.name)
        TestCase.assertEquals("192.168.1.145", serverStorageTest.host)
        TestCase.assertEquals(22, serverStorageTest.port)
        TestCase.assertEquals("username_test", serverStorageTest.username)
        TestCase.assertEquals("PaSSw0rd", serverStorageTest.password)
        TestCase.assertEquals("/home/admin", serverStorageTest.remotePath)
        TestCase.assertNotNull(serverStorageTest.description)
        TestCase.assertNotNull(serverStorageTest.createdAt)
        Assert.assertNull(serverStorageTest.updatedAt)
    }

    @Test
    fun mustReturnAllErrors_whenFieldsAreInvalid() {

        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            ServerStorage(
                name = "",
                host = "",
                port = 0,
                username = "",
                password = "",
                remotePath = "",
                fingerprint = null,
            )
        }

        val message = exception.message!!

        TestCase.assertTrue(message.contains("Server name cannot be empty"))
        TestCase.assertTrue(message.contains("Host cannot be empty"))
        TestCase.assertTrue(message.contains("Username cannot be empty"))
        TestCase.assertTrue(message.contains("Password cannot be empty"))
        TestCase.assertTrue(message.contains("Port must be between 1 and 65535"))
    }


}