/*
 * Developed by Nhan Cao on 8/26/19 10:23 AM.
 * Last modified 8/26/19 10:23 AM.
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

/**
 * Created by nhancv on 1/12/17.
 */

open class NExplicitPermission @JvmOverloads constructor(
        // Activity reference for reflection
        protected open val runningActivity: Activity,
        // This flag present that show Toast in App Setting to guide user enable permission manually
        protected val showToastInSetting: Boolean = true,
        protected val explicitPermissionResult: ExplicitPermissionResult? = null) {
    // Request code for all permissions
    private val PERMISSIONS_REQUEST = 0x007
    // This flag for force request permission
    protected var isPersistent: Boolean = false
    // This flag detect user select never ask in force request mode
//    private var neverAskFlag: Boolean = false
    private var neverAskFlagMap: HashMap<String, Boolean> = HashMap()
    // Active permission
    protected var activePermission: String? = null

    /**
     * Go to application setting
     */
    protected fun startInstalledAppDetailsActivity(showToast: Boolean) {
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

    /**
     * Check specific permission is granted
     */
    protected fun checkPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(runningActivity.applicationContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Method to call ExplicitPermissionResult interface
     *
     * @param isGranted true if permission granted false otherwise
     */
    protected open fun callInterface(isGranted: Boolean) {
        try {
            if (activePermission != null) {
                explicitPermissionResult?.onExplicitPermissionResult(activePermission!!, isGranted)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * This method is called in onRequestPermissionsResult of the runningActivity
     *
     * @param requestCode The request code of runningActivity.
     * @param permissions The requested permissions of runningActivity.
     * @param grantResults The grant results for the corresponding permissions from runningActivity
     */
    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>,
                                   grantResults: IntArray) {
        try {
            if (requestCode == PERMISSIONS_REQUEST) {
                val permissionIndex = permissions.indexOf(activePermission)
                if (grantResults.isNotEmpty() && permissionIndex != -1) {
                    if (grantResults[permissionIndex] == PackageManager.PERMISSION_GRANTED) {
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

    /**
     * Method to request permission with force flag
     *
     * @param permission String
     * @param persistent Boolean
     */
    fun requestPermission(permission: String, persistent: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPersistent = persistent
            activePermission = permission

            val permissionGranted = checkPermissionGranted(permission)
            if (permissionGranted) {
                callInterface(true)
            } else {
                if (neverAskFlagMap[permission] == null || !neverAskFlagMap[permission]!!) {
                    ActivityCompat.requestPermissions(runningActivity, arrayOf(permission), PERMISSIONS_REQUEST)
                    // Check never ask again flag
                    neverAskFlagMap[permission] = !persistent || !runningActivity.shouldShowRequestPermissionRationale(activePermission!!) || permissionGranted
                } else {
                    startInstalledAppDetailsActivity(showToastInSetting)
                }
            }
        }
    }

}