/*
 * Developed by Nhan Cao on 8/25/19 9:44 AM.
 * Last modified 8/25/19 9:44 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.sample;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.nhancv.npermission.NDefaultPermission;
import com.nhancv.npermission.OnPermissionResult;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnPermissionResult {

    private NDefaultPermission nDefaultPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // @nhancv 2019-08-25: request camera permission
        nDefaultPermission = new NDefaultPermission(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nDefaultPermission.forceAllPermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nDefaultPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(@NotNull String permission, boolean isGranted) {
//        2019-08-25 21:54:12.240 16257-16257/com.nhancv.demo E/CAMERA: granted
//        2019-08-25 21:54:16.776 16257-16257/com.nhancv.demo E/WRITE_EXTERNAL_STORAGE: granted
//        2019-08-25 21:54:16.777 16257-16257/com.nhancv.demo E/READ_EXTERNAL_STORAGE: granted
    }
}
