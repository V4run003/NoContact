package com.nxet.nocontact.Daatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.Interfaces.DataDao

@Database(entities = [Data::class], version = 1, exportSchema = false)
abstract class DataDatabase : RoomDatabase() {

    abstract fun dataDao(): DataDao

    companion object {
        private var INSTANCE: DataDatabase? = null

        fun getDatabase(context: Context): DataDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataDatabase::class.java,
                    "data_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}