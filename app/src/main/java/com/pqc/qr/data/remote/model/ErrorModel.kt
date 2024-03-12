package com.pqc.qr.data.remote.model

/**
 * Created by Jeetesh Surana.
 */
data class ErrorModel(
    var message: String = "",
    var errno: String? = null,
    var code: Int = 0
)