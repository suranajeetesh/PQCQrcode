package com.pqc.qr.core.uI

import androidx.fragment.app.Fragment
import com.pqc.qr.utils.extensionFunction.hideKeyboard

/**
 * Created by JeeteshSurana.
 */

abstract class BaseFragment : Fragment() {
    override fun onPause() {
        activity?.hideKeyboard()
        super.onPause()
    }

    override fun onDestroy() {
        activity?.hideKeyboard()
        super.onDestroy()
    }
}