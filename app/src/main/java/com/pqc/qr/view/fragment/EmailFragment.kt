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
import com.pqc.qr.databinding.FragmentEmailBinding
import com.pqc.qr.utils.Contast
import com.pqc.qr.utils.EditTextValidator
import com.pqc.qr.utils.extensionFunction.MoveUtils
import com.pqc.qr.utils.extensionFunction.createEMailString
import com.pqc.qr.utils.extensionFunction.gotoBack
import com.pqc.qr.utils.extensionFunction.multiLineText
import com.pqc.qr.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [EmailFragment] factory method to
 * create an instance of this fragment.
 */
class EmailFragment : BaseFragment() {

    private lateinit var binding: FragmentEmailBinding
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
        binding = FragmentEmailBinding.inflate(inflater, container, false)
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
            imgItem.apply { setImageResource(R.drawable.email) }
            txtEmail.apply { text = resources.getString(R.string.email) }
            edtEmail.apply { hint = resources.getString(R.string.email_hint) }
            txtSubject.apply { text = resources.getString(R.string.subject) }
            edtSubject.apply { hint = resources.getString(R.string.subject) }
            txtMessage.apply { text = context.getString(R.string.message) }
            edtMessage.apply {
                multiLineText()
                hint =resources.getString(R.string.message)
            }
        }
        manageClick(binding)
    }

    private fun manageClick(binding: FragmentEmailBinding) {
        binding.imgBack.setOnClickListener {
            gotoBack()
        }
        binding.btnQr.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val subject = binding.edtSubject.text.toString().trim()
            val data = binding.edtMessage.text.toString().trim()

            if (!EditTextValidator.isValidEmail(email)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.invalid_email), Toast.LENGTH_LONG)
                    .show()

            } else if (!EditTextValidator.isTextFieldValid(subject)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.enter_subject), Toast.LENGTH_LONG).show()
            } else if (!EditTextValidator.isTextFieldValid(data)) {
                Toast.makeText(requireActivity(), resources.getString(R.string.enter_message), Toast.LENGTH_LONG).show()

            } else {
                val message = email.createEMailString(subject, data)
                lifecycleScope.launch {
                    homeViewModel.qrData = message
                    MoveUtils.moveGenerate(requireActivity())
                }
            }
        }
    }
}