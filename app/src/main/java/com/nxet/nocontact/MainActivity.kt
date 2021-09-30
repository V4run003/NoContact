package com.nxet.nocontact

import android.R.attr
import android.R.attr.*
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.hbb20.CountryCodePicker
import com.nxet.nocontact.Adapters.ListAdapter
import com.nxet.nocontact.DataClasses.Data
import java.util.*
import java.util.Locale.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var numberEditText : EditText
    lateinit var countryEditText: EditText
    lateinit var LabelEditText: EditText
    lateinit var sendmsgButton: RelativeLayout
    lateinit var ccp : CountryCodePicker
    private lateinit var mDataViewModel: DataViewModel
    lateinit var tempList: List<Data>
    lateinit var  list: List<Data>
    lateinit var adapter: ListAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()














        sendmsgButton.setOnClickListener{
            ccp.registerCarrierNumberEditText(countryEditText)
            val country = ccp.selectedCountryCode
            val number = numberEditText.text.toString()
            val label = LabelEditText.text.toString()

            instertTodatabase(country , number , label)

                val url = "https://api.whatsapp.com/send?phone=$country$number"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
        }
    }

    private fun instertTodatabase(country: String, number: String, label: String) {
        if (inputCheck(country,number,label)){
            val combine = country+number
            val data = Data(0, combine,label)
            mDataViewModel.addData(data)

            Toast.makeText(this,"Created Successfully",Toast.LENGTH_SHORT).show()

        } else
        {
            Toast.makeText(this,"Fill out Forms",Toast.LENGTH_SHORT).show()
        }

    }

    private fun inputCheck(country: String, number: String, label: String): Boolean {
        return !(TextUtils.isEmpty(country)&&TextUtils.isEmpty(number)&& TextUtils.isEmpty(label))

    }


    fun init(){


        numberEditText = findViewById(R.id.number_editText)
        sendmsgButton = findViewById(R.id.sendMsg_btn)
        countryEditText = findViewById(R.id.country_code)
        ccp = findViewById(R.id.ccp)
        LabelEditText = findViewById(R.id.label_editText)
        mDataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        val adapter =  ListAdapter(applicationContext)
        val recyclerView  : RecyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mDataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        mDataViewModel.readAllData.observe(this, Observer {data->
            val list: List<Data> = data
            Collections.reverse(list)
            adapter.setData(list)
            tempList = list
        })


        val collapsingToolbarLayout :CollapsingToolbarLayout= findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setTitle("No Contact,")
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
    }



}


