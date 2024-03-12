package com.pqc.qr.utils.extensionFunction

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.pqc.qr.R
import com.pqc.qr.utils.Contast.ArgumentsKeys.KEY_DASHBOARD_ITEM_NAME
import com.pqc.qr.utils.Contast.ArgumentsKeys.KEY_QR_IMAGE
import com.pqc.qr.view.fragment.AppFragment
import com.pqc.qr.view.fragment.ContactFragment
import com.pqc.qr.view.fragment.DashboardFragment
import com.pqc.qr.view.fragment.EmailFragment
import com.pqc.qr.view.fragment.GenerateQrFragment
import com.pqc.qr.view.fragment.PhoneFragment
import com.pqc.qr.view.fragment.SmsFragment
import com.pqc.qr.view.fragment.TextFragment
import com.pqc.qr.view.fragment.WebFragment

/**
 * Created by Jeetesh surana.
 */
object MoveUtils {
    fun moveDashboard(activity: FragmentActivity?) {
        activity?.addReplaceFragment(
            R.id.fl_container,
            DashboardFragment(),
            addFragment = false,
            addToBackStack = false
        )
    }

    fun moveGeneraterQr(activity: FragmentActivity?, name: String, position: Int) {
        when (position) {
            0 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    WebFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }

            1 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    PhoneFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }

            2 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    TextFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }

            3 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    SmsFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }

            4 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    EmailFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }

            5 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    ContactFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }

            6 -> {
                activity?.addReplaceFragment(
                    R.id.fl_container,
                    AppFragment().apply {
                        arguments = Bundle().apply {
                            putString(KEY_DASHBOARD_ITEM_NAME, name)
                        }
                    },
                    addFragment = true,
                    addToBackStack = true
                )
            }


        }
    }
    fun moveQr(activity: FragmentActivity?, image: Bitmap) {
        activity?.addReplaceFragment(
            R.id.fl_container,
            GenerateQrFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_QR_IMAGE, image)
                }
            },
            addFragment = true,
            addToBackStack = true
        )
    }

    fun moveGenerate(activity: FragmentActivity?) {
        activity?.addReplaceFragment(
            R.id.fl_container,
            GenerateQrFragment(),
            addFragment = true,
            addToBackStack = true
        )
    }
}