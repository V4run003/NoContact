package com.nxet.nocontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.hbb20.CountryCodePicker
import com.nxet.nocontact.Adapters.ListAdapter
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.Interfaces.RecyclerViewClick
import java.util.*
import java.util.Locale.getDefault
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), RecyclerViewClick {

    lateinit var numberEditText: EditText
    lateinit var countryEditText: EditText
    lateinit var LabelEditText: EditText
    lateinit var sendmsgButton: RelativeLayout
    lateinit var ccp: CountryCodePicker
    private lateinit var mDataViewModel: DataViewModel
    lateinit var adapter: ListAdapter
    lateinit var searchEdittext: EditText
    lateinit var textWatcher: TextWatcher
    lateinit var recyclerView: RecyclerView
    var list = emptyList<Data>()
    lateinit var empty: List<Data>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ListAdapter(applicationContext, this)
        init()

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    filter(s.toString())
                    Log.e("teatext3", "tee")
                    searchEdittext.isFocusable = true
                } else {
                    adapter.filterList(empty as java.util.ArrayList<Data>)
                }
            }
        }

        searchEdittext.addTextChangedListener(textWatcher)

        sendmsgButton.setOnClickListener {
            ccp.registerCarrierNumberEditText(countryEditText)
            val country = ccp.selectedCountryCode
            val number = numberEditText.text.toString()
            val label = LabelEditText.text.toString()

            if (number.isNotEmpty() && containsDigit(number)) {
                if (label.isNotEmpty()) {
                    instertTodatabase(country, number, label)

                    val url = "https://api.whatsapp.com/send?phone=$country$number"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                } else {
                    LabelEditText.error = "Enter a Label"
                    LabelEditText.isFocused
                    Toast.makeText(this, "Enter a Lable", Toast.LENGTH_SHORT).show()
                }


            } else {
                numberEditText.error = "Enter a valid number"
                numberEditText.isFocused
                Toast.makeText(this, "Enter a Valid Number", Toast.LENGTH_SHORT).show()
            }


        }


    }


    private fun instertTodatabase(country: String, number: String, label: String) {
        if (inputCheck(country, number, label)) {
            val combine = country + number
            val data = Data(0, combine, label)
            mDataViewModel.addData(data)
            // Toast.makeText(this,"Created Successfully",Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(country: String, number: String, label: String): Boolean {
        return !(TextUtils.isEmpty(country) && TextUtils.isEmpty(number) && TextUtils.isEmpty(label))

    }


    fun init() {


        numberEditText = findViewById(R.id.number_editText)
        sendmsgButton = findViewById(R.id.sendMsg_btn)
        countryEditText = findViewById(R.id.country_code)
        ccp = findViewById(R.id.ccp)
        LabelEditText = findViewById(R.id.label_editText)
        mDataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        searchEdittext = findViewById(R.id.search_label)
        recyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        mDataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        mDataViewModel.readAllData.observe(this, Observer { data ->
            val list: List<Data> = data
            Collections.reverse(list)
            adapter.setData(list)
            empty = list
        })

        val collapsingToolbarLayout: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setTitle("No Contact.")
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)


    }

    override fun onItemClick(number: String) {

        val url = "https://api.whatsapp.com/send?phone=$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)

    }


    private fun filter(text: String) {
        //new array list that will hold the filtered data
        val filteredNames = ArrayList<Data>()
        //looping through existing elements and adding the element to filtered list
        empty.filterTo(filteredNames) {
            //if the existing elements contains the search input
            it.label.lowercase(getDefault()).contains(text.lowercase(getDefault()))
        }
        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filteredNames)
    }

    private fun containsDigit(text: String): Boolean {
        val numeric = text.matches("-?\\d+(\\.\\d+)?".toRegex())
        return numeric

    }


}


