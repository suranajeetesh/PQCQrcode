package com.pqc.qr.utils.helper.accessFramework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.util.Pair
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.pqc.qr.R
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.FILE_MODE
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.doesFolderExist
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Objects

class ScopeStorageHelper {
    private var content: String? = null
    private var fileName: String? = null
    private var activity: FragmentActivity? = null
    private var observer: ScopeStorageLifecycleObserver? = null
    private var scopeStorageHelperInterface: ScopeStorageHelperInterface? = null
    var folderUri: Uri? = null
    var currentFileUri: Uri? = null
    var isFromPdf = false
    var folderFilesList = ArrayList<Pair<String?, Uri?>>()
    private var fileFindingInterface: FileFindingInterface? = null
    private var folderFindingInterface: FolderFindingInterface? = null
    private val scopeStorageLifecycleObserverInterface: ScopeStorageLifecycleObserver.ScopeStorageLifecycleObserverInterface =
        object : ScopeStorageLifecycleObserver.ScopeStorageLifecycleObserverInterface {
            override fun onFolderCreate(result: ActivityResult) {
                Log.e("TAG", "onFolderCreate() data--> " + result.data)
                if (result.data != null) {
                    folderUri = result.data!!.data
                    if (folderUri != null) {
                        folderFilesList = getAllFiles(folderUri)
                    }
                    if (isFromPdf && folderFindingInterface != null) {
                        folderFindingInterface!!.dataFound(folderUri)
                    }
                    scopeStorageHelperInterface!!.onFolderCreate(folderUri)
                } else {
                    scopeStorageHelperInterface!!.onErrorCreate(
                        activity?.let {
                            ContextCompat.getString(
                                it,
                                R.string.failed_to_create_directory
                            )
                        }
                    )
                }
            }

            override fun onHoursReset(result: ActivityResult?) {
                scopeStorageHelperInterface!!.onHoursReset(result)
            }

            override fun onFileCreate(result: ActivityResult) {
                Log.e("TAG", "onFileCreate() data--> " + result.data)
                if (result.data != null && result.data!!.data != null) {
                    createFile(activity, result.data!!.data)
                    currentFileUri = result.data!!.data
                    scopeStorageHelperInterface!!.onFileCreate(currentFileUri)
                    if (isFromPdf && fileFindingInterface != null) {
                        fileFindingInterface!!.dataFound(
                            Pair(
                                getOriginalFileName(
                                    currentFileUri,
                                    activity
                                ), currentFileUri
                            )
                        )
                        //                            isFromPdf=false;
                    }
                } else {
                    scopeStorageHelperInterface!!.onErrorCreate(
                        activity?.let {
                            ContextCompat.getString(
                                it,
                                R.string.failed_to_create_file
                            )
                        }
                    )
                }
            }

            override fun onReadFile(result: ActivityResult) {
                if (result.data != null) {
                    scopeStorageHelperInterface!!.onReadFile(readFile(result.data!!.data))
                }
            }

            override fun onDeleteFile(result: ActivityResult) {
                if (result.data != null) {
                    deleteFile(result.data!!.data)
                }
            }

            override fun onGetAllFiles(result: ActivityResult) {
                if (result.data != null) {
                    folderUri = result.data!!.data
                    scopeStorageHelperInterface!!.onGetAllFiles(getAllFiles(folderUri), folderUri)
                }
            }
        }

    fun onCreate(
        activity: FragmentActivity,
        scopeStorageHelperInterface: ScopeStorageHelperInterface?
    ) {
        Log.e("Tag", "helper onCreate()--> ")
        this.activity = activity
        this.scopeStorageHelperInterface = scopeStorageHelperInterface
        observer = ScopeStorageLifecycleObserver(activity.activityResultRegistry)
        observer!!.setListener(scopeStorageLifecycleObserverInterface)
        activity.lifecycle.addObserver(observer!!)
    }

    fun createFolder(folderName: String?, fileName: String?, content: String?) {
        this.fileName = fileName
        this.content = content
        observer!!.createFolder(
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .putExtra(Intent.EXTRA_TITLE, folderName)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        )
    }

    fun createFolder(folderName: String?) {
        observer!!.createFolder(
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .putExtra(Intent.EXTRA_TITLE, folderName)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        )
    }

    fun createFile(fileName: String, content: String?, FILE_EXTENSION: String) {
        this.fileName = fileName
        this.content = content
        observer!!.createFile(
            Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*")
                .putExtra(Intent.EXTRA_TITLE, fileName + FILE_EXTENSION)
        )
    }

    fun readFile() {
        observer!!.readFile(
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("*/*")
        )
    }

    fun getAllFiles(folderName: String?) {
        observer!!.getAllFiles(
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .putExtra(Intent.EXTRA_TITLE, folderName)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        )
    }

    fun updateFile(content: String) {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                currentFileUri?.let { activity?.getContentResolver()?.openFileDescriptor(it, FILE_MODE) }
            if (parcelFileDescriptor != null) {
                val fileOutputStream: FileOutputStream =
                    FileOutputStream(parcelFileDescriptor.getFileDescriptor())
                fileOutputStream.write(content.toByteArray())
                fileOutputStream.close()
                parcelFileDescriptor.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteFile() {
        observer!!.deleteFile(
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("*/*")
        )
    }

    fun hoursReset(intent: Intent?) {
        observer!!.hoursReset(intent)
    }

    @SuppressLint("Recycle")
    fun deleteFile(uri: Uri?) {
        try {
            Objects.requireNonNull<Cursor>(
                uri?.let { activity?.getContentResolver()?.query(it, null, null, null, null) }
            ).close()
            if (uri != null) {
                activity?.getContentResolver()?.let { DocumentsContract.deleteDocument(it, uri) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Recycle", "Range")
    fun getAllFiles(uri: Uri?): ArrayList<Pair<String?, Uri?>> {
        val fileNames = ArrayList<Pair<String?, Uri?>>()
        var cursor: Cursor? = null
        try {
            val childrenUri: Uri = DocumentsContract.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            )
            val cr: ContentResolver? = activity?.getContentResolver()
            if (uri != null) {
                cr?.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            cursor = activity?.getContentResolver()?.query(
                childrenUri,
                arrayOf<String>(
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID
                ),
                null,
                null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val fileName =
                        cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                    val documentId =
                        cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID))
                    val fileUri: Uri = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)
                    fileNames.add(Pair(fileName, fileUri))
                } while (cursor.moveToNext())
            } else {
                Log.d("FileListActivity", "Folder is empty.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileNames
    }

    fun folderExistOrCreate(
        folderName: String?,
        folderFindingInterface: FolderFindingInterface?,
        isFromPdf: Boolean
    ) {
        this.isFromPdf = isFromPdf
        this.folderFindingInterface = folderFindingInterface
        if (folderUri != null && doesFolderExist(activity, folderUri)) {
            folderFindingInterface?.dataFound(folderUri)
        } else {
            folderFindingInterface?.folderUriNotFound()
            createFolder(folderName)
        }
    }

    fun fileExistOrCreate(
        fileName: String,
        fileFindingInterface: FileFindingInterface,
        isFromPdf: Boolean,
        FILE_EXTENSION: String
    ) {
        this.isFromPdf = isFromPdf
        this.fileFindingInterface = fileFindingInterface
        if (folderUri == null) {
            fileFindingInterface.notFound(ERROR_IN_FINDING.FOLDER_NOT_FOUND)
            return
        }
        if (currentFileUri == null) {
            manageFileExist(fileName, fileFindingInterface, FILE_EXTENSION)
        } else {
            val currentFileName = getOriginalFileName(currentFileUri, activity)
            if (currentFileName != null && currentFileName.equals(
                    fileName + FILE_EXTENSION,
                    ignoreCase = true
                )
            ) {
                fileFindingInterface.dataFound(Pair(currentFileName, currentFileUri))
            } else {
                manageFileExist(fileName, fileFindingInterface, FILE_EXTENSION)
            }
        }
    }

    private fun manageFileExist(
        fileName: String,
        fileFindingInterface: FileFindingInterface,
        FILE_EXTENSION: String
    ) {
        isFileExist(fileName, object : FileFindingInterface {
            override fun dataFound(data: Pair<String?, Uri?>) {
                currentFileUri = data.second
                fileFindingInterface.dataFound(data)
            }

            override fun notFound(error: ERROR_IN_FINDING) {
                if (error == ERROR_IN_FINDING.DATA_NOT_FOUND) {
                    //Creating blank files
                    createFile(fileName, "  ", FILE_EXTENSION)
                }
                fileFindingInterface.notFound(error)
            }
        }, FILE_EXTENSION)
    }

    fun isFileExist(
        fileName: String,
        fileFindingInterface: FileFindingInterface,
        FILE_EXTENSION: String
    ) {
        folderFilesList = ArrayList(getAllFiles(folderUri))
        if (folderFilesList.isEmpty()) {
            fileFindingInterface.notFound(ERROR_IN_FINDING.DATA_NOT_FOUND)
        } else {
            for (i in folderFilesList.indices) {
                if (folderFilesList[i].first!!.endsWith(FILE_EXTENSION)) {
                    if (folderFilesList[i].first.equals(
                            fileName + FILE_EXTENSION,
                            ignoreCase = true
                        )
                    ) {
                        fileFindingInterface.dataFound(folderFilesList[i])
                        break
                    }
                }
                if (i == folderFilesList.size - 1 && !folderFilesList[i].first.equals(
                        fileName + FILE_EXTENSION,
                        ignoreCase = true
                    )
                ) {
                    fileFindingInterface.notFound(ERROR_IN_FINDING.DATA_NOT_FOUND)
                }
            }
        }
    }

    fun getOriginalFileName(uri: Uri?, context: Context?): String? {
        var cursor: Cursor? = null
        try {
            val contentResolver: ContentResolver = context!!.contentResolver
            cursor = uri?.let { contentResolver.query(it, null, null, null, null) }
            if (cursor != null && cursor.moveToFirst()) {
                val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                return cursor.getString(nameColumnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun createFile(activity: Activity?, uri: Uri?) {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                activity!!.contentResolver.openFileDescriptor(
                    uri!!, FILE_MODE
                )
            if (parcelFileDescriptor != null) {
                val fileOutputStream: FileOutputStream =
                    FileOutputStream(parcelFileDescriptor.getFileDescriptor())
                fileOutputStream.write(content!!.toByteArray())
                fileOutputStream.close()
                parcelFileDescriptor.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFile(uri: Uri?): String {
        val line = StringBuilder()
        try {
            val inputStreamReader =
                InputStreamReader(uri?.let { activity?.getContentResolver()?.openInputStream(it) })
            val bufferedReader = BufferedReader(inputStreamReader)
            var readLine: String?
            while (bufferedReader.readLine().also { readLine = it } != null) {
                line.append(readLine)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return line.toString()
    }

    enum class ERROR_IN_FINDING {
        FOLDER_NOT_FOUND,
        DATA_NOT_FOUND
    }

    interface FileFindingInterface {
        fun dataFound(data: Pair<String?, Uri?>)
        fun notFound(error: ERROR_IN_FINDING) {}
    }

    interface FolderFindingInterface {
        fun dataFound(data: Uri?)
        fun folderUriNotFound() {}
    }

    interface ScopeStorageHelperInterface {
        fun onReadFile(result: String?)
        fun onFolderCreate(folderUri: Uri?)
        fun onFileCreate(fileUri: Uri?)
        fun onHoursReset(intent: ActivityResult?)
        fun onGetAllFiles(list: ArrayList<Pair<String?, Uri?>>?, folderUri: Uri?)
        fun onErrorCreate(error: String?) {}
    }
}
