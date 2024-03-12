package com.pqc.qr.utils.helper

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pqc.qr.utils.Contast.RewardedAd.AD_UNIT_ID_REWARDED_AD
import com.pqc.qr.utils.Contast.RewardedAd.AD_UNIT_ID_INTERSTITIAL_AD

/**
 * Created by Jeetesh Surana.
 *  Make sure for banner add import in xml should be this xmlns:ads="http://schemas.android.com/apk/res-auto"
 */
class AdmobHelperTwoAD(private var activity: FragmentActivity) {
    private var isLoading: Boolean = false;
    private var rewardedAd: RewardedAd? = null
    private var mInterstitialAd: InterstitialAd? = null

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: InterstitialAdLoadCallback? = null

    // private var adRequest: AdManagerAdRequest? = null

    init {
        MobileAds.initialize(activity) {
            loadRewardedAd()
//            adRequest = AdManagerAdRequest.Builder().build()
        }
    }

    fun init() {
        val adRequest = AdManagerAdRequest.Builder().build()
        InterstitialAd.load(activity, "/6499/example/interstitial", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })

        RewardedAd.load(
            activity,
            "/6499/example/rewarded",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("TAG", adError.message.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("TAG", "Ad was loaded.")
                    rewardedAd = ad
                }
            })

    }

    private fun loadRewardedAd() {
        if (mInterstitialAd==null){
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(activity, AD_UNIT_ID_INTERSTITIAL_AD, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        mInterstitialAd = null
                    }
                })

        }
        if (rewardedAd == null) {
            isLoading = true
            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                activity,
                AD_UNIT_ID_REWARDED_AD,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("TAG", adError.message)
                        isLoading = false
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.e("TAG", "Ad was loaded.")
                        super.onAdLoaded(ad)
                        rewardedAd = ad
                        isLoading = false
                    }

                }
            )
        }
    }

    /*fun showBannerAd(view: AdView) {
        val adMobHelperListener: AdMobHelperListener? = null
        adRequest?.let {
            view.loadAd(it)
        } ?: run {
            adMobHelperListener?.onError("The rewarded ad wasn't ready yet.")

            Log.e("TAG", "The rewarded ad wasn't ready yet.")
        }
    }
*/
    fun interstitialAd(){
        Log.e("TAG", mInterstitialAd.toString())
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
            mInterstitialAd=null
        } else {
            loadRewardedAd()
        }

    }
    fun showVideoAd() {
        val adMobHelperListener: AdMobHelperListener? = null
        rewardedAd?.let { ad ->
            ad.show(activity, OnUserEarnedRewardListener { rewardItem ->
                adMobHelperListener?.rewardedAd(rewardItem)
                val rewardAmount = rewardItem.amount
                Log.e("TAG1", "rewardType-->${rewardItem.type}+rewardAmount-->${rewardAmount}")
                rewardedAd = null
                loadRewardedAd()
            })
        } ?: run {
            this.isLoading
            adMobHelperListener?.onError("The rewarded ad wasn't ready yet.")
        }
    }

    interface AdMobHelperListener {
        fun rewardedAd(rewardItem: RewardItem)
        fun onError(message: String?)
    }
}