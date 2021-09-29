package com.nxet.nocontact.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nxet.nocontact.DataClasses.Data

@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addData(data : Data)

    @Query("SELECT * FROM data_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<Data>>
}