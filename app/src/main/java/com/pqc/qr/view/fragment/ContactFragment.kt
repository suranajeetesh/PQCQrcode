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
import com.pqc.qr.databinding.FragmentContactBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.EditTextValidator
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.createVCardString
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ContactFragment] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : BaseFragment() {
    private lateinit var binding: FragmentContactBinding
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
        binding = FragmentContactBinding.inflate(inflater, container, false)
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
            imgItem.apply { setImageResource(R.drawable.contact) }
            txtName.apply { text = resources.getString(R.string.name) }
            edtName.apply { hint = resources.getString(R.string.name_hint)}
            txtNumber.apply { text = resources.getString(R.string.phonenumber) }
            edtNumber.apply {
                hint = context.getString(R.string.phonenumber)
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_PHONE
            }
            txtEmail.apply { text = resources.getString(R.string.email) }
            edtEmail.apply { hint = resources.getString(R.string.email_hint) }
        }
        manageClick(binding)
    }

    private fun manageClick(binding: FragmentContactBinding) {
        binding.imgBack.setOnClickListener {
            gotoBack()
        }
        binding.btnQr.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val number = binding.edtNumber.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()

            if (!EditTextValidator.isValidPhoneNumber(number)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_phone_number), Toast.LENGTH_LONG)
                    .show()

            } else if (!EditTextValidator.isTextFieldValid(name)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.enter_name), Toast.LENGTH_LONG).show()

            } else if (!EditTextValidator.isValidEmail(email)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_email), Toast.LENGTH_LONG).show()
            } else {
                val qrData = number.createVCardString(number, name, email)
                lifecycleScope.launch {
                    homeViewModel.qrData = qrData
                    MoveUtils.moveGenerate(requireActivity())
                }
            }

        }
    }
}
