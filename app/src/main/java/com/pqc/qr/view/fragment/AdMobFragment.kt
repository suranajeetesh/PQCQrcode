package com.pqc.qr.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pqc.qr.R
import com.pqc.qr.core.uI.BaseFragment
import com.pqc.qr.databinding.FragmentAdMobBinding
import com.pqc.qr.databinding.FragmentDashboardBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AdMobFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdMobFragment : BaseFragment() {
    private lateinit var binding: FragmentAdMobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAdMobBinding.inflate(inflater, container, false)
        return binding.root
    }

}