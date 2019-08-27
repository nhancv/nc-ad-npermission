/*
 * Developed by Nhan Cao on 8/25/19 9:44 AM.
 * Last modified 8/25/19 9:44 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nhancv.npermission.AllPermissionResult
import com.nhancv.npermission.NAllPermission


class MainActivity : AppCompatActivity(), AllPermissionResult {

    private lateinit var nAllPermission: NAllPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // @nhancv 2019-08-25: Create permission controller
        nAllPermission = NAllPermission(this,
                allPermissionResult = this)
    }

    override fun onResume() {
        super.onResume()
        nAllPermission.forceAllPermissionGranted()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        nAllPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAllPermissionGranted() {
        Log.e(TAG, "onAllPermissionGranted: isAllGranted")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
