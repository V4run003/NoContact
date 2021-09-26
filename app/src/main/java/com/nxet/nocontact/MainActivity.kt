package com.nxet.nocontact

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.content.res.Resources.Theme
import android.util.Log
import androidx.core.view.get
import com.hbb20.CountryCodePicker
import com.nxet.nocontact.Helpers.DatabaseHelper
import kotlin.math.log10


class MainActivity : AppCompatActivity() {

    lateinit var numberEditText : EditText
    lateinit var countryEditText: EditText
    lateinit var sendmsgButton: RelativeLayout
    lateinit var ccp : CountryCodePicker
    lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()










        sendmsgButton.setOnClickListener{
            ccp.registerCarrierNumberEditText(countryEditText)
            val country = ccp.selectedCountryCode
            val number = numberEditText.text.toString()
            databaseHelper.insertdata(country+number)
                val url = "https://api.whatsapp.com/send?phone=$country$number"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
        }
    }

    fun init(){

        numberEditText = findViewById(R.id.number_editText)
        sendmsgButton = findViewById(R.id.sendMsg_btn)
        countryEditText = findViewById(R.id.country_code)
        ccp = findViewById(R.id.ccp)
        databaseHelper = DatabaseHelper(this)
        val collapsingToolbarLayout :CollapsingToolbarLayout= findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setTitle("No Contact,")
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
    }


}


