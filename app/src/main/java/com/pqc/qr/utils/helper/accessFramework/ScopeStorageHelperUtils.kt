package com.pqc.qr.utils.helper.accessFramework

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile

/**
 * Created by Jeetesh Surana.
 */
object ScopeStorageHelperUtils {
    const val REQUEST_CODE_FOLDER_CREATE = "1001"
    const val REQUEST_CODE_FILE_CREATE = "1002"
    const val REQUEST_CODE_FILE_READ = "1003"
    const val REQUEST_CODE_FILE_DELETE = "1004"
    const val REQUEST_CODE_GET_ALL_FILES = "1005"
    const val REQUEST_CODE_HOURS_RESET = "1006"
    const val JPG_FILE_EXTENSION = ".jpg"
    const val FOLDER_NAME = "PQC QR CODE"
    const val FILE_MODE = "rw"
    fun doesFileExist(context: Context?, uri: Uri?): Boolean {
        if (uri == null) {
            return false
        }
        val documentFile = DocumentFile.fromSingleUri(context!!, uri)
        return documentFile != null && documentFile.exists()
    }

    fun doesFolderExist(context: Context?, parentFolderUri: Uri?): Boolean {
        if (parentFolderUri == null) {
            return false
        }
        val parentFolder = DocumentFile.fromTreeUri(context!!, parentFolderUri)
        return parentFolder != null && parentFolder.exists()
    }

    fun getOriginalFileName(uri: Uri?, context: Context): String? {
        var cursor: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                return cursor.getString(nameColumnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}
