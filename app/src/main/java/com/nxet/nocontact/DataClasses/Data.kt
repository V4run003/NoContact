package com.nxet.nocontact.DataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_table")
data class Data(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val number: String,
    val label: String


)

