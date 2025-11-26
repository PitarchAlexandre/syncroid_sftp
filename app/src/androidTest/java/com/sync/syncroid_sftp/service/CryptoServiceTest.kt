package com.sync.syncroid_sftp.service

import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import org.junit.Assert.*

class CryptoServiceTest {

    @Test
    fun mustEncryptedInformation_returnCryptedInformation() {

        val word = "A-Super_great-word!"
        val cryptoService = CryptoService()

        val encryptedOutputStream = ByteArrayOutputStream()
        val encryptedWord = cryptoService.encrypt(word.toByteArray(Charsets.UTF_8), encryptedOutputStream)

        assertNotSame(word, encryptedWord)
    }

    @Test
    fun mustEncryptInformation_decryptInformation_returnDecryptedInformation() {
        val word = "A-Super_great-word!"
        val cryptoService = CryptoService()

        val encryptedOutputStream = ByteArrayOutputStream()
        cryptoService.encrypt(word.toByteArray(Charsets.UTF_8), encryptedOutputStream)

        val encryptedBytesWithIv = encryptedOutputStream.toByteArray()
        val decryptedWord = cryptoService.decrypt(ByteArrayInputStream(encryptedBytesWithIv)).toString(Charsets.UTF_8)

        assertEquals(word, decryptedWord)
    }

    @Test
    fun encryptToBase64_and_decryptFromBase64_shouldReturnOriginalString() {
        val cryptoService = CryptoService()
        val password = "SuperSecretPassword123!"

        val encrypted = cryptoService.encryptToBase64(password)
        assertNotNull(encrypted)
        assertNotEquals(password, encrypted)

        val decrypted = cryptoService.decryptFromBase64(encrypted)
        assertEquals(password, decrypted)
    }

    @Test(expected = Exception::class)
    fun decryptFromBase64_withInvalidInput_shouldThrow() {
        val cryptoService = CryptoService()
        cryptoService.decryptFromBase64("invalid_base64_string")
    }
}