/*
 * Developed by Nhan Cao on 8/25/19 8:27 PM.
 * Last modified 8/25/19 8:27 PM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission

/**
 * Interface to notify permission result
 */
interface ExplicitPermissionResult {
    /**
     * Method will get called after permission request
     * Do not re-call request permission to avoid infinite loop
     *
     * @param permission asked permission
     * @param isGranted true if permission granted false otherwise
     */
    fun onExplicitPermissionResult(permission: String, isGranted: Boolean)

}
