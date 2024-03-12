package com.pqc.qr.utils.bindingAdapter

import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
/**
 * Created by Jeetesh Surana.
 */

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()
    defaultValue?.let { data.value = it }
    return data
}
