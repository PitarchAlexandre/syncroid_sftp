package com.sync.syncroid_sftp.service

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.InputStream

class ServerStorageServiceTest {

    private lateinit var mockJsch: JSch
    private lateinit var mockSession: Session
    private lateinit var mockChannel: ChannelSftp
    private lateinit var service: ServerStorageService

    @Before
    fun setUp() {
        mockJsch = mockk()
        mockSession = mockk(relaxed = true)
        mockChannel = mockk(relaxed = true)

        service = ServerStorageService(jsch = mockJsch)
    }

    @Test
    fun connectToServer_returnsTrue_whenConnectionSucceeds() {
        val server = ServerStorage(
            name = "Valid Server",
            host = "localhost",
            port = 22,
            username = "user",
            password = "pass",
            remotePath = "/remote/path"
        )

        // Mocks des appels
        every { mockJsch.getSession(server.username, server.host, server.port) } returns mockSession
        every { mockSession.setPassword(server.password) } just Runs
        every { mockSession.setConfig(any<java.util.Properties>()) } just Runs
        every { mockSession.connect(10_000) } just Runs
        every { mockSession.openChannel("sftp") } returns mockChannel
        every { mockChannel.connect(10_000) } just Runs
        every { mockChannel.disconnect() } just Runs
        every { mockSession.disconnect() } just Runs

        val result = service.connectToServer(server)

        assertTrue(result)

        verifySequence {
            mockJsch.getSession(server.username, server.host, server.port)
            mockSession.setPassword(server.password)
            mockSession.setConfig(any<java.util.Properties>())
            mockSession.connect(10_000)
            mockSession.openChannel("sftp")
            mockChannel.connect(10_000)
            mockChannel.disconnect()
            mockSession.disconnect()
        }
    }

    @Test
    fun uploadStream_returnsTrue_onSuccessfulUpload() {

        val server = ServerStorage(
            name = "TestServer",
            host = "localhost",
            port = 22,
            username = "user",
            password = "pass",
            remotePath = "/remote/path"
        )

        val mockJsch = mockk<JSch>()
        val mockSession = mockk<Session>(relaxed = true)
        val mockChannel = mockk<ChannelSftp>(relaxed = true)

        every {
            mockJsch.getSession(server.username, server.host, server.port)
        } returns mockSession

        every { mockSession.setPassword(server.password) } just Runs
        every { mockSession.setConfig(any<java.util.Properties>()) } just Runs
        every { mockSession.connect(10_000) } just Runs
        every { mockSession.openChannel("sftp") } returns mockChannel

        every { mockChannel.connect(10_000) } just Runs
        every { mockChannel.cd("/remote/path") } just Runs

        every { mockChannel.put(any<InputStream>(), "test.txt") } just Runs

        every { mockChannel.disconnect() } just Runs
        every { mockSession.disconnect() } just Runs

        val service = ServerStorageService(jsch = mockJsch)

        // Dummy InputStream
        val fakeStream = "hello".byteInputStream()

        val result = service.uploadStream(
            server,
            fakeStream,
            "/remote/path",
            "test.txt"
        )

        assertTrue(result)

        verifySequence {
            mockJsch.getSession(server.username, server.host, server.port)
            mockSession.setPassword(server.password)
            mockSession.setConfig(any<java.util.Properties>())
            mockSession.connect(10_000)
            mockSession.openChannel("sftp")
            mockChannel.connect(10_000)
            mockChannel.cd("/remote/path")
            mockChannel.put(any<InputStream>(), "test.txt")
            mockChannel.disconnect()
            mockSession.disconnect()
        }
    }

}