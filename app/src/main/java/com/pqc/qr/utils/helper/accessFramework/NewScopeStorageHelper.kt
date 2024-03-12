package com.pqc.qr.utils.helper.accessFramework

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.util.Log
import android.util.Pair
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pqc.qr.R
import com.pqc.qr.data.remote.model.local.DataFound
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.FILE_MODE
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.FOLDER_NAME
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.doesFolderExist
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.getOriginalFileName
import com.pqc.qr.utils.preferenceProvider.PreferenceProvider
import com.pqc.qr.utils.preferenceProvider.PreferenceProviderUtils.KEY_FOLDER
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Objects

/**
 * Created by Jeetesh Surana.
 */
class NewScopeStorageHelper(private var activity: FragmentActivity) {
    private var observer: ScopeStorageLifecycleObserver? = null
    private var scopeStorageHelperInterface: ScopeStorageHelperInterface? = null
    var mainFolderUri: Uri? = null
    var currentFileUri: Uri? = null
    var isRedirectFile = false
    var fileCreate: FileCreate? = null
    var fileFindingInterface: FileFindingInterface? = null
    private var preferenceProvider: PreferenceProvider? = null
    private var folderFilesList = ArrayList<DataFound>()

    fun onCreate(
        activity: FragmentActivity,
        scopeStorageHelperInterface: ScopeStorageHelperInterface?
    ) {
        Log.e("Tag", "helper onCreate()--> ")
        this.activity = activity
        this.scopeStorageHelperInterface = scopeStorageHelperInterface
        ScopeStorageLifecycleObserver(activity.activityResultRegistry).apply {
            observer = this
            setListener(scopeStorageLifecycleObserverInterface)
            activity.lifecycle.addObserver(this)
        }
        managePreferenceProvider(activity)
    }

    fun onFragmentCreate(
        activity: Fragment,
        scopeStorageHelperInterface: ScopeStorageHelperInterface?
    ) {
        Log.e("Tag", "helper onCreate()--> ")
        this.activity = activity.requireActivity()
        this.scopeStorageHelperInterface = scopeStorageHelperInterface
        ScopeStorageLifecycleObserver(activity.requireActivity().activityResultRegistry).apply {
            observer = this
            setListener(scopeStorageLifecycleObserverInterface)
            activity.lifecycle.addObserver(this)
        }
        managePreferenceProvider(activity.requireActivity())
    }

    private fun managePreferenceProvider(activity: FragmentActivity) {
        preferenceProvider = PreferenceProvider(activity)
        mainFolderUri = getFolderUri()
    }

    private fun setFileCreate(fileName: String?, content: String?, FILE_EXTENSION: String?) {
        fileCreate = fileName?.let { name ->
            FILE_EXTENSION?.let { fileExtension ->
                FileCreate(
                    name, content,
                    fileExtension
                )
            }
        }
    }

    fun setFolderUri() {
        preferenceProvider?.putString(KEY_FOLDER, mainFolderUri.toString())
    }

    fun getFolderUri(): Uri? {
        val folderUri: String? = preferenceProvider?.getString(KEY_FOLDER, "")
        return if (folderUri.isNullOrEmpty()) {
            null
        } else {
            Uri.parse(folderUri)
        }
    }

    private val scopeStorageLifecycleObserverInterface: ScopeStorageLifecycleObserver.ScopeStorageLifecycleObserverInterface =
        object : ScopeStorageLifecycleObserver.ScopeStorageLifecycleObserverInterface {
            override fun onFolderCreate(result: ActivityResult) {
                if (result.data != null && result.data?.data != null) {
                    mainFolderUri = result.data?.data
                    setFolderUri()
                    initFolderList()
                    if (fileCreate != null) {
                        createFileIfFileNotExist()
                    }
                    scopeStorageHelperInterface?.onFolderCreate(mainFolderUri)
                } else {
                    scopeStorageHelperInterface?.onErrorCreate(
                        ContextCompat.getString(
                            activity,
                            R.string.failed_to_create_directory
                        )
                    )
                }
            }

            override fun onHoursReset(result: ActivityResult?) {
                scopeStorageHelperInterface?.onHoursReset(result)
            }

            override fun onFileCreate(result: ActivityResult) {
                if (result.data != null) {
                    val data = result.data?.data
                    currentFileUri = data
                    scopeStorageHelperInterface?.onFileCreate(data)
                    //fileFindingInterface?.dataFound(Pair(getOriginalFileName(data, activity), data))
                    fileFindingInterface?.dataFound(DataFound(getOriginalFileName(data, activity), data))
                } else {
                    scopeStorageHelperInterface?.onErrorCreate(
                        ContextCompat.getString(activity, R.string.failed_to_create_file)
                    )
                }
            }

            override fun onReadFile(result: ActivityResult) {
                if (result.data != null) {
                    scopeStorageHelperInterface?.onReadFile(readFile(result.data?.data))
                }
            }

            override fun onDeleteFile(result: ActivityResult) {
                if (result.data != null) {
                    deleteFile(result.data?.data)
                }
            }

            override fun onGetAllFiles(result: ActivityResult) {
                if (result.data != null) {
                    mainFolderUri = result.data?.data
                    scopeStorageHelperInterface?.onGetAllFiles(getAllFiles(mainFolderUri), mainFolderUri)
                }
            }
        }

    private fun createFileIfFileNotExist() {
        isFileExist(fileCreate?.fileName, object : FileFindingInterface {
            override fun dataFound(data: DataFound) {
                scopeStorageHelperInterface?.onFileCreate(data.second)
                fileFindingInterface?.dataFound(data)
            }

            override fun notFound(error: ERROR_IN_FINDING) {
                super.notFound(error)
                if (error == ERROR_IN_FINDING.DATA_NOT_FOUND) {
                    createFile(fileCreate?.fileName, fileCreate?.content, fileCreate?.fileExtension)
                }
            }
        }, fileCreate?.fileExtension)
    }

    private fun initFolderList() {
        if (folderFilesList.isNotEmpty()) {
            folderFilesList.clear()
        }
        folderFilesList = getAllFiles(mainFolderUri)
    }

    fun folderExistOrCreate(): Uri? {
        fileCreate = if (doesFolderExist(activity, mainFolderUri)) {
            return mainFolderUri
        } else {
            Log.e("Tag", "folderExistOrCreate()--> " + "folderUri not exist")
            createFolder(FOLDER_NAME)
            null
        }
        return null
    }

    fun fileExistOrCreate(
        fileName: String,
        FILE_EXTENSION: String,
        fileFindingInterface: FileFindingInterface?
    ) {
        this.fileFindingInterface = fileFindingInterface
        Log.e("Tag", "fileExistOrCreate()--> " + "Checking folder while creating ")
        if (!checkingFolderIsExistWhileFileCreating(fileName, FILE_EXTENSION)) {
            fileCreate = FileCreate(fileName, FILE_EXTENSION)
            createFileIfFileNotExist()
        }
    }

    private fun isFileExist(
        fileName: String?,
        fileFindingInterface: FileFindingInterface,
        FILE_EXTENSION: String?) {
        folderFilesList = ArrayList(getAllFiles(mainFolderUri))
        if (folderFilesList.isEmpty()) {
            fileFindingInterface.notFound(ERROR_IN_FINDING.DATA_NOT_FOUND)
        } else {
            for (i in folderFilesList.indices) {
                if (FILE_EXTENSION?.let { folderFilesList[i].first?.endsWith(it) } == true) {
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

    private fun checkingFolderIsExistWhileFileCreating(
        fileName: String,
        FILE_EXTENSION: String
    ): Boolean {
        if (!doesFolderExist(activity, mainFolderUri)) {
            fileCreate = FileCreate(fileName, FILE_EXTENSION)
            Log.e("Tag", "checkingFolderIsExistWhileFileCreating()--> " + "Folder uri not exist")
            createFolder(FOLDER_NAME)
            return true
        } else {
            fileCreate = null
        }
        return false
    }

    fun createFolder(folderName: String) {
        Log.e("Tag", "createFolder()--> $folderName")
        fileCreate = null
        observer?.createFolder(
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .putExtra(Intent.EXTRA_TITLE, folderName)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        )
    }

    fun createFile(fileName: String?, content: String?, FILE_EXTENSION: String?) {
        if (fileCreate == null) {
            setFileCreate(fileName, content, FILE_EXTENSION)
        }
        observer?.createFile(
            Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*")
                .putExtra(Intent.EXTRA_TITLE, fileName + FILE_EXTENSION)
                . addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        )
    }

    fun updateBitmapFile(uri: Uri?,content: Bitmap?) {
        try {
            val parcelFileDescriptor =
                uri?.let { activity.contentResolver?.openFileDescriptor(it, FILE_MODE) }
            val fileOutputStream = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
            val byteArrayOutputStream = ByteArrayOutputStream()
            content?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            fileOutputStream.write(byteArray)
            fileOutputStream.close()
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Recycle")
    fun deleteFile(uri: Uri?) {
        try {
            Objects.requireNonNull<Cursor>(
                uri?.let { activity.contentResolver.query(it, null, null, null, null) }
            ).close()
            uri?.let { DocumentsContract.deleteDocument(activity.contentResolver, it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readFile() {
        observer?.readFile(
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("*/*")
        )
    }

    fun readFile(uri: Uri?): String {
        val line = StringBuilder()
        try {
            val inputStreamReader =
                InputStreamReader(uri?.let { activity.contentResolver.openInputStream(it) })
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

    fun updateFile(content: String) {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                currentFileUri?.let { activity.contentResolver.openFileDescriptor(it, FILE_MODE) }
            if (parcelFileDescriptor != null) {
                val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
                fileOutputStream.write(content.toByteArray())
                fileOutputStream.close()
                parcelFileDescriptor.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun hoursReset(intent: Intent?) {
        observer?.hoursReset(intent)
    }

    @SuppressLint("Recycle", "Range")
    fun getAllFiles(uri: Uri?): ArrayList<DataFound> {
        val fileNames: ArrayList<DataFound> = ArrayList()

       // val fileNames = ArrayList<Pair<String, Uri>>()
        val cursor: Cursor?
        try {
            val contentResolver = activity.contentResolver
            val childrenUri: Uri = DocumentsContract.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            )
            val cr: ContentResolver = activity.contentResolver
            uri?.let {
                cr.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            cursor = activity.contentResolver.query(
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
                    val thumbnail = getThumbnail(contentResolver, fileUri)
                    fileNames.add(DataFound(fileName, fileUri, thumbnail))
                } while (cursor.moveToNext())
            } else {
                Log.d("FileListActivity", "Folder is empty.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileNames
    }
    private fun getThumbnail(contentResolver: ContentResolver, fileUri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    enum class ERROR_IN_FINDING {
        FOLDER_NOT_FOUND,
        DATA_NOT_FOUND
    }

    interface ScopeStorageHelperInterface {
        fun onReadFile(result: String?) {}
        fun onFolderCreate(folderUri: Uri?) {}
        fun onFileCreate(fileUri: Uri?) {}
        fun onHoursReset(intent: ActivityResult?) {}
        fun onGetAllFiles(list: ArrayList<DataFound>, folderUri: Uri?) {}
        fun onErrorCreate(error: String?) {}
    }

    interface FileFindingInterface {
        fun dataFound(data: DataFound)
        fun notFound(error: ERROR_IN_FINDING) {}
    }
}
