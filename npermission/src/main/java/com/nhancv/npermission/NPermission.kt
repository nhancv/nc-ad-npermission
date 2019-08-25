/*
 * Developed by Nhan Cao on 8/25/19 8:52 PM.
 * Last modified 8/25/19 8:52 PM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission

interface NPermission {
    /**
     * Get all needed permissions
     * @return List<String>
     */
    var getAllNeededPermissions: List<String>

    /**
     * Force all permission must be granted. The permission will be fetched from manifests
     */
    fun forceAllPermissionGranted()

    /**
     * Method to request permission
     *
     * @param permission String
     */
    fun requestPermission(permission: String)

    /**
     * Method to request permission with force flag
     *
     * @param permission String
     * @param persistent Boolean
     */
    fun requestPermission(permission: String, persistent: Boolean)

    /**
     * This method is called in onRequestPermissionsResult of the runningActivity
     *
     * @param requestCode The request code of runningActivity.
     * @param permissions The requested permissions of runningActivity.
     * @param grantResults The grant results for the corresponding permissions from runningActivity
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)

}