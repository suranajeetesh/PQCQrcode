package com.pqc.qr.view.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.pqc.qr.R
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.databinding.FragmentPhoneBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.EditTextValidator
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.createPhoneNumberString
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [PhoneFragment] factory method to
 * create an instance of this fragment.
 */
class PhoneFragment : BaseFragment() {
    private lateinit var binding: FragmentPhoneBinding
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
        binding = FragmentPhoneBinding.inflate(inflater, container, false)
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
            imgItem.apply { setImageResource(R.drawable.phone) }
            txtUrl.apply { text = title?.uppercase() }
            edtUrl.apply {
                hint = resources.getString(R.string.number_hint)
                inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
        manageClick(binding)
    }

    private fun manageClick(binding: FragmentPhoneBinding) {
        binding.imgBack.setOnClickListener {
            gotoBack()
        }
        binding.btnQr.setOnClickListener {
            val dataField = binding.edtUrl.text.toString().trim()
            if (!EditTextValidator.isValidPhoneNumber(dataField)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_phone_number), Toast.LENGTH_LONG)
                    .show()
            } else {
                val qrData = dataField.createPhoneNumberString(dataField)
                lifecycleScope.launch {
                    homeViewModel.qrData = qrData
                    MoveUtils.moveGenerate(requireActivity())
                }
            }
        }
    }
}