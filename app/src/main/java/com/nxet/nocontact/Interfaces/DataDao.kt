package com.nxet.nocontact.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nxet.nocontact.DataClasses.Data

@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addData(data: Data)

    @Query("SELECT * FROM data_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Data>>

    @Delete
    suspend fun deleteData(data: Data)

    @Query("DELETE FROM data_table")
    suspend fun deleteAlldata()
}