package com.pqc.qr.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pqc.qr.R
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.databinding.BottomSheetDialogAppBinding
import com.pqc.qr.databinding.FragmentAppBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.createPlayStoreDeepLink
import com.pqc.qr.utils.extensionFunction.extractPackageNameFromAppStoreUrl
import com.pqc.qr.utils.extensionFunction.extractPackageNameFromPlayStoreUrl
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.utils.extensionFunction.isPackageNameValidAppStore
import com.pqc.qr.utils.extensionFunction.isPackageNameValidPlayStore
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [AppFragment] factory method to
 * create an instance of this fragment.
 */
class AppFragment : BaseFragment() {
    private lateinit var binding: FragmentAppBinding
    private var title: String? = null
    private var dialog: BottomSheetDialog? = null
    private lateinit var sheetBinding: BottomSheetDialogAppBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.getString(Contast.ArgumentsKeys.KEY_DASHBOARD_ITEM_NAME)?.let { title = it }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppBinding.inflate(inflater, container, false)
        sheetBinding = BottomSheetDialogAppBinding.inflate(layoutInflater)
        dialog?.setContentView(sheetBinding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            init()
        }
    }

    private fun init() {
        dialog = BottomSheetDialog(requireContext())
        var clickListener = true
        sheetBinding.txtAppStore.text = resources.getString(R.string.app_store)
        sheetBinding.txtPlayStore.text = resources.getString(R.string.play_store)
        dialog?.setContentView(sheetBinding.root)
        dialog?.show()
        sheetBinding.clAppstore.setOnClickListener {
            clickListener = false
            binding.apply {
                txtTitle.apply { text = resources.getString(R.string.app_store) }
                imgItem.apply {
                    setImageResource(R.drawable.ic_app_store)
                }
                txtApp.apply { text = resources.getString(R.string.app_store) }
                edtApp.apply { hint = resources.getString(R.string.url) }
            }
            manageClick(binding)
        }
        sheetBinding.clPlaystore.setOnClickListener {
            clickListener = false
            binding.apply {
                txtTitle.apply { text = resources.getString(R.string.play_store) }
                imgItem.apply {
                    setImageResource(R.drawable.ic_google_play_store)
                }
                txtApp.apply { text = resources.getString(R.string.play_store) }
                edtApp.apply { hint = resources.getString(R.string.url) }
            }
            manageClick(binding)
        }
        dialog?.setOnDismissListener {
            if (clickListener) {
                gotoBack()
                clickListener = false
            }
        }
    }

    private fun manageClick(binding: FragmentAppBinding) {
        dialog?.dismiss()
        binding.imgBack.setOnClickListener {
            gotoBack()
        }
        binding.btnQr.setOnClickListener {
            val appUrl = binding.edtApp.text.toString().trim()
            var message = ""
            if (URLUtil.isValidUrl(appUrl)) {
                val packageNameAppStore = appUrl.extractPackageNameFromAppStoreUrl()
                val packageNamePlayStore = appUrl.extractPackageNameFromPlayStoreUrl()
                if (packageNameAppStore != null && binding.txtTitle.text ==resources.getString(R.string.app_store)) {
                    packageNameAppStore.createPlayStoreDeepLink().let { message = it }
                    message = appUrl
                } else if (packageNamePlayStore != null && binding.txtTitle.text == resources.getString(R.string.play_store)) {
                    packageNamePlayStore.createPlayStoreDeepLink().let { message = it }
                }
            } else if (appUrl.isPackageNameValidAppStore() && binding.txtTitle.text == resources.getString(R.string.app_store)) {
                appUrl.createPlayStoreDeepLink(true).let { message = it }
            } else if (appUrl.isPackageNameValidPlayStore() && binding.txtTitle.text == resources.getString(R.string.play_store))
                appUrl.createPlayStoreDeepLink().let { message = it }
            if (message != "") {
                lifecycleScope.launch {
                    homeViewModel.qrData = message
                    MoveUtils.moveGenerate(requireActivity())
                }
            } else {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_app_URL), Toast.LENGTH_LONG).show()
            }
        }
    }
}