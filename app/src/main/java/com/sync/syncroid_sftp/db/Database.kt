package com.sync.syncroid_sftp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sync.syncroid_sftp.data.serverstorage.ServerStorage
import com.sync.syncroid_sftp.data.serverstorage.ServerStorageDao

@Database(
    entities = [ServerStorage::class],
    version = 2,
    exportSchema = true
)
abstract class SyncroidDatabase : RoomDatabase() {

    abstract fun serverStorageDao(): ServerStorageDao

    companion object {
        @Volatile
        private var INSTANCE: SyncroidDatabase? = null

        val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE servers_storage_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                host TEXT NOT NULL,
                port INTEGER NOT NULL,
                username TEXT NOT NULL,
                password TEXT NOT NULL,
                remotePath TEXT NOT NULL DEFAULT '.',
                description TEXT,
                fingerprint TEXT,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER
            )
        """.trimIndent())

                database.execSQL("""
            INSERT INTO servers_storage_new (
                id, name, host, port, username, password, remotePath, description, createdAt, updatedAt
            )
            SELECT 
                id, name, host, port, username, password, remotePath, description, createdAt, updatedAt
            FROM servers_storage
        """.trimIndent())

                database.execSQL("DROP TABLE servers_storage")

                database.execSQL("ALTER TABLE servers_storage_new RENAME TO servers_storage")
            }
        }


        fun getDatabase(context: Context): SyncroidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncroidDatabase::class.java,
                    "syncroid_database",

                    ).addMigrations(migration_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}
