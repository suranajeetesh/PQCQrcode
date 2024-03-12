package com.pqc.qr.utils.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.pqc.qr.R
import com.pqc.qr.utils.extensionFunction.getQRCodeWithOverlay
import java.util.EnumMap

/**
 * Created by Jeetesh Surana.
 */
class QrGenerateHelper(
    private val context: Context,
    private val qrGenerateHelperListener: QrGenerateHelperListener
) {

    fun qrGenerate(data: String, height: Int = 450, width: Int = 450) {
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 1
        try {
            val barcodeEncoder = BarcodeEncoder()
            val qrCodeBitmap = barcodeEncoder.encodeBitmap(
                data,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.qrlogo)
            val scaledLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, width / 3, height / 3, false)
            //Log.e("TAG","onQrGenerate() --> ")
            qrGenerateHelperListener.onQrGenerate(
                getQRCodeWithOverlay(
                    qrCodeBitmap,
                    scaledLogoBitmap
                )
            )
        } catch (e: Exception) {
            qrGenerateHelperListener.onError(e.message.toString())
        }
    }

    interface QrGenerateHelperListener {
        fun onQrGenerate(bitmap: Bitmap)
        fun onError(message: String?)
    }
}