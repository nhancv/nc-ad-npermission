/*
 * Developed by Nhan Cao on 8/25/19 9:44 AM.
 * Last modified 8/25/19 9:41 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.reflect.Method

/**
 * Created by nhancao on 1/12/17.
 */

class NDefaultPermission @JvmOverloads constructor(// Activity reference for reflection
        private val runningActivity: Activity?, // This flag present that show Toast in App Setting to guide user enable permission manually
        private val showToastInSetting: Boolean = true,
        override var getAllNeededPermissions: List<String> = ArrayList()) : NPermission {
    // Request code for all permissions
    private val PERMISSIONS_REQUEST = 0x007
    // All needed permission
    private var allNeededPermissions: MutableList<String>? = null
    // This flag for force request permission
    private var isPersistent: Boolean = false
    // This flag detect user select nerver ask in force request mode
    private var neverAskFlag: Boolean = false
    // Active permission
    private lateinit var activePermission: String

    private val requiredPermissions: List<String>
        get() {
            try {
                if (runningActivity != null) {
                    val info = runningActivity.packageManager.getPackageInfo(
                            runningActivity.packageName,
                            PackageManager.GET_PERMISSIONS
                    )
                    val requestedPermissions = info.requestedPermissions
                    if (requestedPermissions != null && requestedPermissions.isNotEmpty()) {
                        val myStringList = ArrayList<String>(requestedPermissions.size)
                        myStringList.addAll(listOf(*requestedPermissions))
                        return myStringList
                    }
                }
                return ArrayList()
            } catch (ex: Exception) {
                return ArrayList()
            }

        }

    init {
        allNeededPermissions = ArrayList()
        val allPermissions = requiredPermissions
        for (permission in allPermissions) {
            if (!checkPermissionGranted(permission)) {
                allNeededPermissions!!.add(permission)
            }
        }
    }

    /**
     * Go to application setting
     */
    fun startInstalledAppDetailsActivity(showToast: Boolean) {
        if (runningActivity == null) {
            return
        }
        neverAskFlag = true
        if (showToast) {
            Toast.makeText(runningActivity, runningActivity.resources.getText(R.string.enable_yourself), Toast.LENGTH_SHORT).show()
        }
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + runningActivity.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        runningActivity.startActivity(i)
    }

    private fun checkPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(runningActivity!!.applicationContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Method to call OnPermissionResult interface
     *
     * @param isGranted true if permission granted false otherwise
     * @throws InterfaceNotImplementedException throws when OnPermissionResult is not implemented
     */
    @Throws(InterfaceNotImplementedException::class)
    private fun callInterface(isGranted: Boolean) {
        if (runningActivity == null) {
            return
        }

        val method: Method
        try {
            method = runningActivity.javaClass.getMethod("onPermissionResult", String::class.java, Boolean::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            throw InterfaceNotImplementedException(
                    "Please implement OnPermissionResult interface in your activity to get the permissions result")
        }

        try {
            if (isPersistent && !isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (runningActivity.shouldShowRequestPermissionRationale(activePermission)) {
                    requestPermission(activePermission, isPersistent)
                } else {
                    startInstalledAppDetailsActivity(showToastInSetting)
                }
            } else {
                method.invoke(runningActivity, activePermission, isGranted)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        try {
            if (requestCode == PERMISSIONS_REQUEST) {
                val permissionIndex = permissions.indexOf(activePermission)
                if (grantResults.isNotEmpty() &&
                        permissionIndex != -1) {
                    if (grantResults[permissionIndex] == PackageManager.PERMISSION_GRANTED) {
                        neverAskFlag = false
                        allNeededPermissions!!.remove(activePermission)
                        callInterface(true)
                    } else {
                        callInterface(false)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun forceAllPermissionGranted() {
        if (allNeededPermissions!!.size > 0) {
            requestPermission(allNeededPermissions!![0])
        }
    }

    override fun requestPermission(permission: String) {
        requestPermission(permission, true)
    }

    override fun requestPermission(permission: String, persistent: Boolean) {
        isPersistent = persistent
        activePermission = permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionGranted = checkPermissionGranted(permission)
            if (!permissionGranted && !neverAskFlag) {
                ActivityCompat.requestPermissions(runningActivity!!, arrayOf(permission), PERMISSIONS_REQUEST)
            } else {
                callInterface(permissionGranted)
            }
        }
    }

    /**
     * Exception throws when OnPermissionResult interface not implemented
     */
    private inner class InterfaceNotImplementedException(message: String) : RuntimeException(message)

}