package com.sync.syncroid_sftp.service

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
@RequiresApi(Build.VERSION_CODES.M)
class CryptoService {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    private fun createEncryptCipher(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        return cipher
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    /**
     * Encrypts a byte array and writes the result to an output stream.
     *
     * This function encrypts the provided data using an AES key from the Android Keystore.
     * The resulting encrypted data, along with its initialization vector (IV), is written
     * to the given `outputStream` in the following format:
     *
     * @param bytes The raw byte array to be encrypted.
     * @param outputStream The stream to which the encrypted data and IV will be written.
     * @return The encrypted byte array (ciphertext only, without the IV).
     */
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val cipher = createEncryptCipher()
        val encryptedBytes = cipher.doFinal(bytes)

        DataOutputStream(outputStream).use { dos ->
            dos.writeInt(cipher.iv.size)
            dos.write(cipher.iv)
            dos.writeInt(encryptedBytes.size)
            dos.write(encryptedBytes)
        }

        return encryptedBytes
    }

    /**
     * Decrypts data from an input stream.
     * The stream is expected to be in a specific format created by the `encrypt` function:
     * first, an integer representing the size of the Initialization Vector (IV),
     * then the IV itself, followed by an integer representing the size of the encrypted data,
     * and finally the encrypted data.
     *
     * @param inputStream The stream to read the encrypted data from.
     * @return A byte array containing the decrypted data.
     */
    fun decrypt(inputStream: InputStream): ByteArray {
        return DataInputStream(inputStream).use { dis ->
            val ivSize = dis.readInt()
            val iv = ByteArray(ivSize)
            dis.readFully(iv)

            val encryptedBytesSize = dis.readInt()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            dis.readFully(encryptedBytes)

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }

    /**
     * Encrypts a byte array and writes the result to an output stream.
     * The output stream will contain the Initialization Vector (IV) followed by the encrypted data.
     * This structure is necessary for later decryption.
     *
     * @param bytes The byte array to be encrypted.
     * @param outputStream The stream to which the encrypted data and its IV will be written.
     * @return The encrypted byte array, not including the IV.
     */
    fun encryptToBase64(clearText: String): String {
        val outputStream = ByteArrayOutputStream()
        encrypt(clearText.toByteArray(Charsets.UTF_8), outputStream)
        val encryptedBytes = outputStream.toByteArray()
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }

    /**
     * Decrypts a Base64 encoded string.
     * The input string is expected to have been encrypted by `encryptToBase64`,
     * containing both the Initialization Vector (IV) and the encrypted data.
     *
     * @param encryptedBase64 The Base64 encoded string to decrypt.
     * @return The original, decrypted clear text string.
     */
    fun decryptFromBase64(encryptedBase64: String): String {
        val encryptedBytesWithIv = android.util.Base64.decode(encryptedBase64, android.util.Base64.DEFAULT)
        val decryptedBytes = decrypt(ByteArrayInputStream(encryptedBytesWithIv))
        return decryptedBytes.toString(Charsets.UTF_8)
    }
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
