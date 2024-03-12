package com.pqc.qr.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.pqc.qr.R
import com.pqc.qr.app.App
import com.pqc.qr.core.uI.BaseActivity
import com.pqc.qr.databinding.ActivityDashboardBinding
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.helper.AppOpenAd
import com.pqc.qr.utils.isInternetAvailable
import com.pqc.qr.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (isInternetAvailable(this)) {
            MoveUtils.moveDashboard(this)
        } else {
            Toast.makeText(this, R.string.not_internet, Toast.LENGTH_SHORT).show();
        }
    }
}