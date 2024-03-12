package com.pqc.qr.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pqc.qr.R
import com.pqc.qr.databinding.FragmentDashboardBinding
import com.pqc.qr.databinding.FragmentQrListBinding


/**
 * A simple [Fragment] subclass.
 * Use the [QrListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QrListFragment : Fragment() {
    private lateinit var binding:FragmentQrListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentQrListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}