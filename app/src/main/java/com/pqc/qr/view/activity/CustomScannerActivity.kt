package com.pqc.qr.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.pqc.qr.databinding.ActivityCustomScannerBinding


class CustomScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomScannerBinding
    private lateinit var capture: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeQrScanner(savedInstanceState)
        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun initializeQrScanner(savedInstanceState: Bundle?) = with(binding) {
        capture = CaptureManager(this@CustomScannerActivity, zxingBarcodeScanner)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)
        capture.decode()

    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(
            keyCode,
            event
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}