package com.nxet.nocontact.Adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.Interfaces.RecyclerViewClick
import com.nxet.nocontact.R
import java.util.*
import android.view.animation.AlphaAnimation





class ListAdapter(applicationContext: Context, private var recyclerViewClick: RecyclerViewClick) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var dataList = emptyList<Data>()
    var context = applicationContext

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {



        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.data_row, parent, false)
        )


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {



        val currentItem = dataList[position]
        val label: TextView = holder.itemView.findViewById(R.id.labelview)
        val number: TextView = holder.itemView.findViewById(com.nxet.nocontact.R.id.number)
        val icon: TextView = holder.itemView.findViewById(R.id.contact_icon)
        setFadeAnimation(holder.itemView)
        if (currentItem.label.isNotEmpty()) {
            val char = currentItem.label.substring(0, 1)

            icon.setText(char.uppercase())
        }


        val temp = currentItem.number

        holder.itemView.setOnClickListener {
            recyclerViewClick.onItemClick(temp)
        }
        holder.itemView.findViewById<RelativeLayout>(R.id.delete_icon_wrap).setOnClickListener {


            recyclerViewClick.deleteItem(currentItem)
        }

        label.text = currentItem.label
        number.text = "+$temp"


    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Data>) {
        this.dataList = data


        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredNames: ArrayList<Data>) {
        this.dataList = filteredNames
        notifyDataSetChanged()
    }

    fun onLongClick(view: View?): Boolean {
        // Handle long click
        // Return true to indicate the click was handled
        return true
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000
        view.startAnimation(anim)
    }


    override fun getItemCount(): Int {


        return dataList.size

    }

}

