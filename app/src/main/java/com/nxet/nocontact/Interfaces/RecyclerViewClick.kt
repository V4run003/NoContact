package com.nxet.nocontact.Interfaces

import com.nxet.nocontact.DataClasses.Data

interface RecyclerViewClick {
    fun onItemClick(number: String)
    fun deleteItem(data: Data)
}