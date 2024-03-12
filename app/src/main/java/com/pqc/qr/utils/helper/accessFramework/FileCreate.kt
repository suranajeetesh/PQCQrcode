package com.pqc.qr.utils.helper.accessFramework

import android.net.Uri

class FileCreate {
    // Getter and Setter methods for fileName
    var fileName: String

    // Getter and Setter methods for content
    var content: String? = null

    // Getter and Setter methods for fileExtension
    var fileExtension: String
    private val fileUri: Uri? = null

    constructor(fileName: String, content: String?, fileExtension: String) {
        this.fileName = fileName
        this.content = content
        this.fileExtension = fileExtension
    }

    constructor(fileName: String, fileExtension: String) {
        this.fileName = fileName
        this.fileExtension = fileExtension
    }
}
