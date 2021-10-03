package com.nxet.nocontact.Repostitory

import androidx.lifecycle.LiveData
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.Interfaces.DataDao

class DataRepository(private val dataDao: DataDao) {

    val readAllData: LiveData<List<Data>> = dataDao.readAllData()

    suspend fun addData(data: Data) {


        dataDao.addData(data)
    }

    suspend fun deleteData(data: Data){
        dataDao.deleteData(data)

    }

    suspend fun deleteAllData(){
        dataDao.deleteAlldata()
    }
}