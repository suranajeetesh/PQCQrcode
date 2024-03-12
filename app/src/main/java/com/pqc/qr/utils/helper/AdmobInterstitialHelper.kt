package com.pqc.qr.utils.helper

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem

/**
 * Created by Jeetesh Surana.
 *  Make sure for banner add import in xml should be this xmlns:ads="http://schemas.android.com/apk/res-auto"
 */
class AdmobInterstitialHelper(private var activity: FragmentActivity) {
    private var isLoading: Boolean = false;
    private var mInterstitialAd: InterstitialAd? = null
    init {
        MobileAds.initialize(activity) {
            init()
        }
    }

    fun init() {
        val adRequest = AdManagerAdRequest.Builder().build()
        InterstitialAd.load(activity,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("TAG1", adError.message.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("TAG", "Ad was loaded.")
                super.onAdLoaded(interstitialAd)
                mInterstitialAd = interstitialAd
            }
        })
    }

    fun showVideoAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    interface AdMobHelperListener {
        fun rewardedAd(rewardItem: RewardItem)
        fun onError(message: String?)
    }
}