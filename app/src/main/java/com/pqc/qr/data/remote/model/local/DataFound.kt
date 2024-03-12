package com.pqc.qr.data.remote.model.local

import android.graphics.Bitmap
import android.net.Uri

/**
 * Created by Jeetesh Surana.
 */
data class DataFound (
    var first: String? = null,
    var second: Uri? =null,
    var third: Bitmap?= null
)