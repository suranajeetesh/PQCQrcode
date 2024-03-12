package com.pqc.qr.view.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.data.remote.model.local.DataFound
import com.pqc.qr.databinding.FragmentGeneraterQrBinding
import com.pqc.qr.utils.Contast.ArgumentsKeys.KEY_QR_IMAGE_DATE_FORMAT
import com.pqc.qr.utils.Contast.ArgumentsKeys.KEY_QR_IMAGE_FILE_NAME
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.utils.extensionFunction.hide
import com.pqc.qr.utils.extensionFunction.show
import com.pqc.qr.utils.helper.accessFramework.NewScopeStorageHelper
import com.pqc.qr.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class GenerateQrFragment : BaseFragment() {

    private lateinit var binding: FragmentGeneraterQrBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var image: Bitmap? = null
    private var newScopeStorageHelper: NewScopeStorageHelper?=null
    private var folderUri: Uri?=null
    private var fileName: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateFormat = SimpleDateFormat(KEY_QR_IMAGE_DATE_FORMAT, Locale.getDefault())
        val currentDateAndTime: String = dateFormat.format(Date())
        fileName = KEY_QR_IMAGE_FILE_NAME +"_"+ currentDateAndTime
        initHelper()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneraterQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        init(binding)
    }

    private fun initObserver() {
        homeViewModel.qrCodeBitmap.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.imgQr.setImageBitmap(it)
                image = it
            }
        }
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
                this@GenerateQrFragment.folderUri = folderUri
//                if (folderUri!=null){
//                    onGetAllFiles(newScopeStorageHelper?.getAllFiles(folderUri), folderUri)
//                }
                if (folderUri!=null) {
                    newScopeStorageHelper?.createFile(fileName, "", ".jpg")
                }
            }

            override fun onFileCreate(fileUri: Uri?) {
                super.onFileCreate(fileUri)
                newScopeStorageHelper?.currentFileUri=fileUri
                newScopeStorageHelper?.updateBitmapFile(fileUri,image)
            }

            override fun onHoursReset(intent: ActivityResult?) {
                super.onHoursReset(intent)
            }

            override fun onGetAllFiles(list: ArrayList<DataFound>, folderUri: Uri?) {
                super.onGetAllFiles(list, folderUri)
//                print(list)
                Log.e("TAG","onGetAllFiles() --> ${list}")
            }

            override fun onErrorCreate(error: String?) {
                super.onErrorCreate(error)
            }
        }

    private fun init(binding: FragmentGeneraterQrBinding) {
        binding.apply { manageClick() }
        lifecycleScope.launch {
            binding.progressbar.show()
            delay(10)
            binding.imgQr.setBackgroundResource(android.R.color.background_light)
            binding.progressbar.hide()
            homeViewModel.qrGenerate()
        }
    }

    private fun FragmentGeneraterQrBinding.manageClick() {
        btnSave.setOnClickListener {
            folderUri = newScopeStorageHelper?.folderExistOrCreate()
////            if (folderUri!=null){
////                scopeStorageHelperInterface.onGetAllFiles(newScopeStorageHelper?.getAllFiles(folderUri), folderUri)
////            }
            if (folderUri!=null) {
                newScopeStorageHelper?.createFile(fileName, "", ".jpg")
            }

           /* if (scopeStorageHelper?.folderUri != null) {
                scopeStorageHelper?.createFile("QRCODE", image)
            } else {
                scopeStorageHelper?.createFolder(FOLDER_NAME, "QRCODE", image)
            }*/
        }
        imgBack.setOnClickListener {
            gotoBack()
        }
        imgDelete.setOnClickListener {
           // scopeStorageHelper?.deleteFile()
        }
        imgUpload.setOnClickListener {
         // val data=  scopeStorageHelper?.readFile()
        }
    }
}