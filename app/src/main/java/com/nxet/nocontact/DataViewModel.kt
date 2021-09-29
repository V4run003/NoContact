package com.nxet.nocontact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nxet.nocontact.Daatabase.DataDatabase
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.Repostitory.DataRepository
import kotlinx.coroutines.launch

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val readAllData : LiveData<List<Data>>
    val repository : DataRepository
    init{
        val dataDao = DataDatabase.getDatabase(application).dataDao()
        repository = DataRepository(dataDao)
        readAllData = repository.readAllData
    }

    fun addData(data : Data){
        viewModelScope.launch {
            repository.addData(data)
        }
    }
}