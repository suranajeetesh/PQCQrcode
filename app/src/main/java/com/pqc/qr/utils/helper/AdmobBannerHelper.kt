package com.pqc.qr.utils.helper

import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest

/**
 * Created by Jeetesh Surana.
 * Make sure for banner add import in xml should be this xmlns:ads="http://schemas.android.com/apk/res-auto"
 */
class AdmobBannerHelper(private var fragment: Fragment) {
    private var adRequest: AdManagerAdRequest? =null

    init {
        fragment.context?.let {
            MobileAds.initialize(it) {
                adRequest = AdManagerAdRequest.Builder().build()
            }
        }
    }

    fun showBannerAd(view: AdView) {
        adRequest?.let {
//            view.setAdSize(AdSize.BANNER)
//            view.adUnitId = "ca-app-pub-9982245873913738/3033166871"
//            view.adSize = AdSize.BANNER
            view.loadAd(it)
        } ?: run {
            Log.e("TAG", "The rewarded ad wasn't ready yet.")
        }
    }
}