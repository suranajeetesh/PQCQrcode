package com.pqc.qr.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.pqc.qr.R
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.databinding.FragmentWebBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.EditTextValidator
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [WebFragment] factory method to
 * create an instance of this fragment.
 */
class WebFragment : BaseFragment() {
    private lateinit var binding: FragmentWebBinding
    private var title: String? = null
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.getString(Contast.ArgumentsKeys.KEY_DASHBOARD_ITEM_NAME)?.let { title = it }
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebBinding.inflate(inflater, container, false)
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
            txtTitle.apply { text = title?.uppercase() }
            imgItem.apply { setImageResource(R.drawable.url) }
            txtUrl.apply { text = title?.uppercase() }
            edtUrl.apply { hint = resources.getString(R.string.url) }
        }
        manageClick(binding)
    }

    private fun manageClick(binding: FragmentWebBinding) {
        binding.imgBack.setOnClickListener {
            gotoBack()
        }
        binding.btnQr.setOnClickListener {
            val qrData = binding.edtUrl.text.toString().trim()
            if (!EditTextValidator.isValidUrl(qrData)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_URl), Toast.LENGTH_LONG).show()
            } else {
                lifecycleScope.launch {
                    homeViewModel.qrData = qrData
                    MoveUtils.moveGenerate(requireActivity())
                }
            }
        }
    }

}