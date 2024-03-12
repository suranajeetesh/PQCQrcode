package com.pqc.qr.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.pqc.qr.core.uI.BaseViewModel
import com.pqc.qr.utils.helper.QrGenerateHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Jeetesh Surana.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val application: Application) : BaseViewModel() {

    private var qrGenerateHelper: QrGenerateHelper? = null
    var qrCodeBitmap = MutableLiveData<Bitmap>()
    var qrData: String = ""

    private var listener = object : QrGenerateHelper.QrGenerateHelperListener {
        override fun onError(message: String?) {
            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        }

        override fun onQrGenerate(bitmap: Bitmap) {
            qrCodeBitmap.postValue(bitmap)
        }
    }

    init {
        qrGenerateHelper = QrGenerateHelper(application, listener)
    }
    fun qrGenerate() {
        qrData.let { qrGenerateHelper?.qrGenerate(it) }
    }

}