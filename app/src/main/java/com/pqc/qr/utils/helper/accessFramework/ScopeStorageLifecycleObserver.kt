package com.pqc.qr.utils.helper.accessFramework

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.REQUEST_CODE_FILE_CREATE
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.REQUEST_CODE_FILE_DELETE
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.REQUEST_CODE_FILE_READ
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.REQUEST_CODE_FOLDER_CREATE
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.REQUEST_CODE_GET_ALL_FILES
import com.pqc.qr.utils.helper.accessFramework.ScopeStorageHelperUtils.REQUEST_CODE_HOURS_RESET

class ScopeStorageLifecycleObserver(registry: ActivityResultRegistry) : DefaultLifecycleObserver {
    private var folderCreate: ActivityResultLauncher<Intent>? = null
    private var fileCreate: ActivityResultLauncher<Intent>? = null
    private var readFile: ActivityResultLauncher<Intent>? = null
    private var getAllFiles: ActivityResultLauncher<Intent>? = null
    private var deleteFile: ActivityResultLauncher<Intent>? = null
    private var hours34Reset: ActivityResultLauncher<Intent>? = null
    private var scopeStorageLifecycleObserverInterface: ScopeStorageLifecycleObserverInterface? =
        null
    private val registry: ActivityResultRegistry

    init {
        this.registry = registry
    }

    override fun onCreate(owner: LifecycleOwner) {
        folderCreate = registry.register(REQUEST_CODE_FOLDER_CREATE,
            owner as LifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            scopeStorageLifecycleObserverInterface!!.onFolderCreate(
                result
            )
        }
        fileCreate = registry.register(REQUEST_CODE_FILE_CREATE,
            owner as LifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            scopeStorageLifecycleObserverInterface!!.onFileCreate(
                result
            )
        }
        readFile = registry.register(REQUEST_CODE_FILE_READ,
            owner as LifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            scopeStorageLifecycleObserverInterface!!.onReadFile(
                result
            )
        }
        deleteFile = registry.register(REQUEST_CODE_FILE_DELETE,
            owner as LifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            scopeStorageLifecycleObserverInterface!!.onDeleteFile(
                result
            )
        }
        getAllFiles = registry.register(REQUEST_CODE_GET_ALL_FILES,
            owner as LifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            scopeStorageLifecycleObserverInterface!!.onGetAllFiles(
                result
            )
        }
        hours34Reset = registry.register(REQUEST_CODE_HOURS_RESET,
            owner as LifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            scopeStorageLifecycleObserverInterface!!.onHoursReset(
                result
            )
        }
    }

    fun setListener(scopeStorageLifecycleObserverInterface: ScopeStorageLifecycleObserverInterface?) {
        this.scopeStorageLifecycleObserverInterface = scopeStorageLifecycleObserverInterface
    }

    fun createFile(intent: Intent?) {
        fileCreate?.launch(intent)
    }

    fun createFolder(intent: Intent?) {
        folderCreate?.launch(intent)
    }

    fun readFile(intent: Intent?) {
        readFile?.launch(intent)
    }

    fun getAllFiles(intent: Intent?) {
        getAllFiles?.launch(intent)
    }

    fun deleteFile(intent: Intent?) {
        deleteFile?.launch(intent)
    }

    fun hoursReset(intent: Intent?) {
        hours34Reset?.launch(intent)
    }

    interface ScopeStorageLifecycleObserverInterface {
        fun onFolderCreate(result: ActivityResult)
        fun onHoursReset(result: ActivityResult?)
        fun onFileCreate(result: ActivityResult)
        fun onReadFile(result: ActivityResult)
        fun onDeleteFile(result: ActivityResult)
        fun onGetAllFiles(result: ActivityResult)
    }
}
