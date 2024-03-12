package com.pqc.qr.view.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.rewarded.RewardItem
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.pqc.qr.R
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.data.remote.model.local.DashboardList
import com.pqc.qr.data.remote.model.local.DataFound
import com.pqc.qr.databinding.FragmentDashboardBinding
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.resultContactString
import com.pqc.qr.utils.extensionFunction.resultEmailString
import com.pqc.qr.utils.extensionFunction.resultPhoneString
import com.pqc.qr.utils.extensionFunction.resultSmsString
import com.pqc.qr.utils.extensionFunction.resultWebString
import com.pqc.qr.utils.helper.AdmobHelper
import com.pqc.qr.utils.helper.accessFramework.NewScopeStorageHelper
import com.pqc.qr.view.activity.CustomScannerActivity
import com.pqc.qr.view.adapter.DashboardListAdapter
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


class DashboardFragment : BaseFragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var dashboardListAdapter: DashboardListAdapter
    private var dashboardList = ArrayList<DashboardList>()
    private var selectedItem = -1
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var admobHelper: AdmobHelper? = null
    private var reward: String? = null
    private var newScopeStorageHelper: NewScopeStorageHelper?=null
    private var folderUri: Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        admobHelper = AdmobHelper(requireActivity())
        initHelper()
    }

    private fun initHelper() {
        newScopeStorageHelper= NewScopeStorageHelper(requireActivity())
        newScopeStorageHelper?.onFragmentCreate(this, scopeStorageHelperInterface)
    }
    private val scopeStorageHelperInterface =
        object : NewScopeStorageHelper.ScopeStorageHelperInterface {
            override fun onReadFile(result: String?) {
                super.onReadFile(result)
            }

            override fun onFolderCreate(folderUri: Uri?) {
                super.onFolderCreate(folderUri)
                this@DashboardFragment.folderUri = folderUri
                if (folderUri!=null){
                    newScopeStorageHelper?.getAllFiles(folderUri)
                        ?.let { onGetAllFiles(it, folderUri) }
                }
            }
            override fun onGetAllFiles(list: ArrayList<DataFound>, folderUri: Uri?) {
                super.onGetAllFiles(list, folderUri)
                for (qr in list)
                    Log.e("TAG","onGetAllFiles() --> name--> ${qr.first} uri--> ${qr.second} thumbnail-->${qr.third}")
            }

            override fun onErrorCreate(error: String?) {
                super.onErrorCreate(error)
            }
        }
    private var listener = object : AdmobHelper.AdMobHelperListener {
        override fun rewardedAd(rewardItem: RewardItem) {
            reward = rewardItem.amount.toString()
            Log.e("TAG", reward.toString())
        }

        override fun onError(message: String?) {
            Log.e("TAG", message.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            dashboardList.add(DashboardList("web", R.drawable.url))
            dashboardList.add(DashboardList("phone", R.drawable.phone))
            dashboardList.add(DashboardList("text", R.drawable.text))
            dashboardList.add(DashboardList("sms", R.drawable.sms))
            dashboardList.add(DashboardList("email", R.drawable.email))
            dashboardList.add(DashboardList("contact", R.drawable.contact))
            dashboardList.add(DashboardList("app", R.drawable.app))
            init()
        }
    }

    private fun init() {
        binding.btnAllQrcode.setOnClickListener {
            //admobHelper?.interstitialAd()
            folderUri = newScopeStorageHelper?.folderExistOrCreate()
            if (folderUri!=null){
                newScopeStorageHelper?.getAllFiles(folderUri)
                    ?.let { it1 -> scopeStorageHelperInterface.onGetAllFiles(it1, folderUri) }
            }else{
                Log.e("TAG","folderUri-->${folderUri}")
            }
        }
        binding.btnGift.setOnClickListener {
            admobHelper?.showVideoAd()
        }
        binding.scannerFab.setOnClickListener {
            launchScanner()
        }
        val layoutManager = GridLayoutManager(context, 3)
        binding.rcList.layoutManager = layoutManager
        dashboardListAdapter =
            DashboardListAdapter(dashboardList, object : DashboardListAdapter.ItemClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun itemClick(position: Int, item: DashboardList) {
                    MoveUtils.moveGeneraterQr(requireActivity(), item.name.toString(), position)
                    if (selectedItem != -1) {
                        dashboardListAdapter.notifyItemChanged(selectedItem)
                    }
                    when (selectedItem) {
                        -1 -> {
                            dashboardList[position].isChecked = true
                            selectedItem = position
                        }

                        position -> {
                            dashboardList[position].isChecked = false
                            selectedItem = -1
                        }

                        else -> {
                            dashboardList[selectedItem].isChecked = false
                            dashboardList[position].isChecked = true
                            selectedItem = position
                        }
                    }
                    dashboardListAdapter.notifyItemChanged(position)
                }
            })
        binding.rcList.adapter = dashboardListAdapter
    }

    private fun launchScanner() {
        val options = ScanOptions().setOrientationLocked(false)
            .setCaptureActivity(CustomScannerActivity::class.java).setCameraId(0)
            .setBeepEnabled(false).setBarcodeImageEnabled(true)
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        barcodeLauncher1.launch(options)
    }

    private val barcodeLauncher1 =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (!result.contents.isNullOrEmpty()) {
                val data = result.contents
                if (URLUtil.isValidUrl(data)) {
                    requireActivity().resultWebString(data)
                } else if (data.contains("mailto")) {
                    requireActivity().resultEmailString(data)
                } else if (data.contains("tel")) {
                    requireActivity().resultPhoneString(data)
                } else if (data.contains("BEGIN")) {
                    requireActivity().resultContactString(data)
                } else if (data.contains("smsto") || data.contains("SMSTO:")) {
                    requireActivity().resultSmsString(data)
                } else {
                    Toast.makeText(requireActivity(), data, Toast.LENGTH_LONG).show()
                }
            } else {
                // CANCELED
                Toast.makeText(context, resources.getString(R.string.cancelled), Toast.LENGTH_LONG)
                    .show()
            }
        }
}