package com.nxet.nocontact.Adapters

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.R
import android.R.attr.data
import java.util.*
import kotlin.Comparator


class ListAdapter(applicationContext: Context) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var dataList = emptyList<Data>()
    var context = applicationContext

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {


        return MyViewHolder(LayoutInflater.from(parent.context).inflate(com.nxet.nocontact.R.layout.data_row ,  parent, false))


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val currentItem  = dataList[position]
        val label : TextView = holder.itemView.findViewById(R.id.labelview)
        val number : TextView = holder.itemView.findViewById(com.nxet.nocontact.R.id.number)
        val icon : TextView = holder.itemView.findViewById(R.id.contact_icon)
        if (currentItem.label.isNotEmpty()){
            val char = currentItem.label.substring(0,1)

            icon.setText(char.uppercase())
        }

        val temp = currentItem.number



        label.setText(currentItem.label)
        number.setText("+$temp")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Data>){
        this.dataList = data


        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {



        return dataList.size

    }
}