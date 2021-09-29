package com.nxet.nocontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.hbb20.CountryCodePicker


class MainActivity : AppCompatActivity() {

    lateinit var numberEditText : EditText
    lateinit var countryEditText: EditText
    lateinit var LabelEditText: EditText
    lateinit var sendmsgButton: RelativeLayout
    lateinit var ccp : CountryCodePicker



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()












        sendmsgButton.setOnClickListener{
            ccp.registerCarrierNumberEditText(countryEditText)
            val country = ccp.selectedCountryCode
            val number = numberEditText.text.toString()
            val label = LabelEditText.text.toString()

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
        LabelEditText = findViewById(R.id.label_editText)

        val collapsingToolbarLayout :CollapsingToolbarLayout= findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setTitle("No Contact,")
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
    }


}


