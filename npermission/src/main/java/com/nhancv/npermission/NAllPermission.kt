/*
 * Developed by Nhan Cao on 8/25/19 9:44 AM.
 * Last modified 8/25/19 9:41 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission

import android.app.Activity
import android.content.pm.PackageManager

/**
 * Created by nhancv on 1/12/17.
 */

class NAllPermission @JvmOverloads constructor(
        // Activity reference for reflection
        runningActivity: Activity,
        // This flag present that show Toast in App Setting to guide user enable permission manually
        showToastInSetting: Boolean = true,
        explicitPermissionResult: ExplicitPermissionResult? = null,
        private val allPermissionResult: AllPermissionResult? = null)
    : NExplicitPermission(runningActivity, showToastInSetting, explicitPermissionResult) {
    // All needed permission
    private var allNeededPermissions: MutableList<String>? = null

    init {
        // Update needed permission list
        allNeededPermissions = ArrayList()
        val allPermissions = requiredPermissions()
        for (permission in allPermissions) {
            if (!checkPermissionGranted(permission)) {
                allNeededPermissions!!.add(permission)
            }
        }
    }

    /**
     * Get all needed permissions
     * @return List<String>
     */
    private fun requiredPermissions(): List<String> {
        try {
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
            return ArrayList()
        } catch (ex: Exception) {
            return ArrayList()
        }
    }

    /**
     * Method to call ExplicitPermissionResult interface
     *
     * @param isGranted true if permission granted false otherwise
     */
    override fun callInterface(isGranted: Boolean) {
        try {
            super.callInterface(isGranted)

            if (isGranted) {
                allNeededPermissions?.remove(activePermission)
                if (allNeededPermissions?.size == 0) {
                    allPermissionResult?.onAllPermissionGranted()
                } else {
                    requestPermission(allNeededPermissions!![0])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Force all permission must be granted. The permission will be fetched from manifests
     */
    fun forceAllPermissionGranted() {
        if (allNeededPermissions!!.size > 0) {
            requestPermission(allNeededPermissions!![0])
        } else {
            callInterface(true)
        }

    }

}