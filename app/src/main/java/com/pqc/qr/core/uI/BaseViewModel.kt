package com.pqc.qr.core.uI

import androidx.lifecycle.ViewModel
import com.pqc.qr.data.remote.model.ErrorModel
import com.pqc.qr.utils.bindingAdapter.mutableLiveData

/**
 * Created by JeeteshSurana.
 */

open class BaseViewModel : ViewModel() {
    var mError = mutableLiveData(ErrorModel())
}