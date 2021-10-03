package com.nxet.nocontact

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hbb20.CountryCodePicker
import com.nxet.nocontact.Adapters.ListAdapter
import com.nxet.nocontact.DataClasses.Data
import com.nxet.nocontact.Interfaces.RecyclerViewClick
import java.util.*
import java.util.Locale.getDefault
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


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
    var mInterstitialAd : InterstitialAd? = null
    var nativeAd : NativeAd? = null
    lateinit var country : String
    lateinit var number : String
    lateinit var label : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ListAdapter(applicationContext, this)
        init()



        MobileAds.initialize(this) { initializationStatus: InitializationStatus ->
            val statusMap =
                initializationStatus.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.d(
                    "MyApp", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status!!.description, status!!.latency
                    )
                )
            }
            LoadInterstitialAds1()
            refreshAd()
        }

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
            country = ccp.selectedCountryCode
             number = numberEditText.text.toString()
             label = LabelEditText.text.toString()

            if (number.isNotEmpty() && containsDigit(number)) {
                if (label.isNotEmpty()) {
                    if (mInterstitialAd!=null){
                        ShowInterstitialAds1()

                    } else{
                        instertTodatabase(country, number, label)
                        val packageManager: PackageManager = this.getPackageManager()
                        val i = Intent(Intent.ACTION_VIEW)
                            val url = "https://api.whatsapp.com/send?phone=$country$number"
                            i.data = Uri.parse(url)
                                this.startActivity(i)
                    }

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
        collapsingToolbarLayout.setExpandedTitleMargin(80,55,15,25)
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)






    }

    override fun onDestroy() {
        if (nativeAd!=null){
            nativeAd!!.destroy()
        }

        if (mInterstitialAd!=null){
            mInterstitialAd = null
        }
        super.onDestroy()
    }

    fun ShowInterstitialAds1() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this)
            mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    instertTodatabase(country, number, label)
                    val url = "https://api.whatsapp.com/send?phone=$country$number"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)

                    //do something when interstitial ad is closed by user, or leave it blank
                }

                override fun onAdShowedFullScreenContent() {


                    //add below line
                    mInterstitialAd = null
                }
            })
        }
    }


    private fun LoadInterstitialAds1() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.admob_interstitial_ad1),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    //progressdialog.dismiss()
                    //ShowInterstitialAds1()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        (Objects.requireNonNull(adView.headlineView) as TextView).text =
            nativeAd.headline
        Objects.requireNonNull(adView.mediaView)
            .setMediaContent(Objects.requireNonNull(nativeAd.mediaContent))
        if (nativeAd.body == null) {
            Objects.requireNonNull(adView.bodyView).visibility = View.INVISIBLE
        } else {
            Objects.requireNonNull(adView.bodyView).visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            Objects.requireNonNull(adView.callToActionView).visibility =
                View.INVISIBLE
        } else {
            Objects.requireNonNull(adView.callToActionView).visibility =
                View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            Objects.requireNonNull(adView.iconView).visibility = View.GONE
        } else {
            (Objects.requireNonNull(adView.iconView) as ImageView).setImageDrawable(nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            Objects.requireNonNull(adView.priceView).visibility = View.INVISIBLE
        } else {
            Objects.requireNonNull(adView.priceView).visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            Objects.requireNonNull(adView.storeView).visibility = View.INVISIBLE
        } else {
            Objects.requireNonNull(adView.storeView).visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            Objects.requireNonNull(adView.starRatingView).visibility =
                View.INVISIBLE
        } else {
            (Objects.requireNonNull(adView.starRatingView) as RatingBar).rating =
                nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            Objects.requireNonNull(adView.advertiserView).visibility =
                View.INVISIBLE
        } else (Objects.requireNonNull(adView.advertiserView) as TextView).text =
            nativeAd.advertiser
        adView.advertiserView.visibility = View.VISIBLE
        adView.setNativeAd(nativeAd)
    }

    @SuppressLint("InflateParams")
    private fun refreshAd() {
        val builder = AdLoader.Builder(this, this.getString(R.string.native_ad_id))
        builder.forNativeAd { unifiedNativeAd: NativeAd ->
            if (nativeAd != null) {
                nativeAd!!.destroy()
            }
            nativeAd = unifiedNativeAd
            val frameLayout = findViewById<FrameLayout>(R.id.fl_adplaceholder)
            val adView =
                layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }.build()
        val adOptions = NativeAdOptions.Builder().build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {}).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    override fun onItemClick(number: String) {

        val url = "https://api.whatsapp.com/send?phone=$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteItem(data: Data) {
        mDataViewModel.deleteData(data)
        adapter.notifyDataSetChanged()
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

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


}


