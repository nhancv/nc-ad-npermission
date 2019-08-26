/*
 * Developed by Nhan Cao on 8/26/19 12:36 AM.
 * Last modified 8/26/19 12:36 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission

/**
 * Interface to notify all permission result
 */
interface AllPermissionResult {
    /**
     * Method will get called after all permission granted.
     * Do not re-call request permission to avoid infinite loop
     */
    fun onAllPermissionGranted()

}
