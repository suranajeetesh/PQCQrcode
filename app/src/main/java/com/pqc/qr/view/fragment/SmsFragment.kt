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
import com.pqc.qr.databinding.FragmentSmsBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.EditTextValidator
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.createSmsString
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.utils.extensionFunction.multiLineText
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [SmsFragment] factory method to
 * create an instance of this fragment.
 */
class SmsFragment : BaseFragment() {

    private lateinit var binding: FragmentSmsBinding
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
        binding = FragmentSmsBinding.inflate(inflater, container, false)
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
            imgItem.apply { setImageResource(R.drawable.sms) }
            txtNumber.apply { text = resources.getText(R.string.phonenumber) }
            edtNumber.apply {
                hint = resources.getString(R.string.hint_phone)
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_PHONE
            }
            txtMessage.apply { text = context.getString(R.string.message) }
            edtMessage.apply {
                multiLineText()
                hint = resources.getString(R.string.message)
            }
        }
        manageClick(binding)
    }

    private fun manageClick(binding: FragmentSmsBinding) {
        binding.imgBack.setOnClickListener {
            gotoBack()
        }
        binding.btnQr.setOnClickListener {
            val number = binding.edtNumber.text.toString().trim()
            val dataField = binding.edtMessage.text.toString().trim()

            if (!EditTextValidator.isValidPhoneNumber(number)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_phone_number), Toast.LENGTH_LONG).show()

            } else if (!EditTextValidator.isTextFieldValid(dataField)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.input_text), Toast.LENGTH_LONG).show()

            } else {
                val qrData = number.createSmsString(dataField)
                lifecycleScope.launch {
                    homeViewModel.qrData = qrData
                    MoveUtils.moveGenerate(requireActivity())
                }
            }
        }
    }
}