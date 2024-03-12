package com.pqc.qr.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.pqc.qr.R
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.databinding.FragmentTextBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.EditTextValidator
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.utils.extensionFunction.multiLineText
import com.pqc.qr.utils.helper.AdmobBannerHelper
import com.pqc.qr.utils.helper.AdmobHelper
import com.pqc.qr.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [TextFragment] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TextFragment : BaseFragment() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentTextBinding
    private var title: String? = null
    private var admobHelper: AdmobHelper? = null

     // private var admobBannerHelper: AdmobBannerHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.getString(Contast.ArgumentsKeys.KEY_DASHBOARD_ITEM_NAME)?.let { title = it }
        super.onCreate(savedInstanceState)
       // admobBannerHelper= AdmobBannerHelper(this)
        admobHelper= AdmobHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            init()
        }
    }

    private fun init() {
        binding.apply {
            admobHelper?.showBannerAd(bannerAd)
            manageClick()
            txtTitle.apply { text = title?.uppercase() }
            imgItem.apply { setImageResource(R.drawable.text) }
            txtUrl.apply { text = title?.uppercase() }
            edtUrl.apply {
                multiLineText()
                hint = resources.getString(R.string.text)
            }
        }
    }

    private fun FragmentTextBinding.manageClick() {
        imgBack.setOnClickListener {
            gotoBack()
        }
        btnQr.setOnClickListener {
            val qrData = edtUrl.text.toString().trim()
            if (title == "text") {
                if (!EditTextValidator.isTextFieldValid(qrData)) {
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.input_text),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    lifecycleScope.launch {
                        homeViewModel.qrData = qrData
                        MoveUtils.moveGenerate(requireActivity())
                    }
                }
            }
        }
    }
}