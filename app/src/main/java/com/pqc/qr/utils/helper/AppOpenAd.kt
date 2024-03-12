package com.pqc.qr.utils.helper

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pqc.qr.app.App
import com.pqc.qr.view.activity.DashboardActivity

/**
 * Created by Jeetesh Surana.
 */
class AppOpenAd(private var activity: App) {

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: InterstitialAdLoadCallback? = null

    init {
        MobileAds.initialize(activity) {
            loadAd()
//            adRequest = AdManagerAdRequest.Builder().build()
        }
    }

    fun inti() {
        val adRequest = AdManagerAdRequest.Builder().build()
        AppOpenAd.load(
            activity,
            "/6499/example/interstitial",
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                }

                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    // Handle the error
                }
            })
    }

    private fun loadAd() {
        if (appOpenAd != null) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // App open ad has been dismissed
                    appOpenAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // App open ad failed to show
                }

                override fun onAdShowedFullScreenContent() {
                    // App open ad has been shown
                }
            }
        }
    }
    fun appOpenAD(dashboardActivity: DashboardActivity) {
        appOpenAd?.show(dashboardActivity)
    }
}