/*
 * Developed by Nhan Cao on 8/25/19 9:44 AM.
 * Last modified 8/25/19 9:41 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by nhancao on 1/12/17.
 */

public class NPermission {
    private static final String TAG = NPermission.class.getSimpleName();
    /**
     * Request code for all permissions
     */
    private final int N_PERMISSIONS_REQUEST = 0x007;
    /**
     * Activity reference for reflection
     */
    private Activity runningActivity;
    private boolean forceRequest;
    private boolean neverAskFlag;

    public NPermission() {
        this(false);
    }

    public NPermission(boolean forceRequest) {
        this.forceRequest = forceRequest;
    }

    /**
     * Go to application setting
     */
    public void startInstalledAppDetailsActivity(final Activity context, final boolean showToast) {
        if (context == null) {
            return;
        }
        neverAskFlag = true;
        if (showToast) {
            Toast.makeText(context, context.getResources().getText(R.string.enable_yourself), Toast.LENGTH_SHORT).show();
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public boolean isForceRequest() {
        return forceRequest;
    }

    public void setForceRequest(boolean forceRequest) {
        this.forceRequest = forceRequest;
    }

    public boolean checkPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(runningActivity.getApplicationContext(),
                permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * method to request permission
     *
     * @param runningActivity current activity reference
     * @param permission permission to ask
     */
    public void requestPermission(Activity runningActivity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.runningActivity = runningActivity;
            if (ContextCompat.checkSelfPermission(runningActivity.getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED && !neverAskFlag) {
                ActivityCompat.requestPermissions(runningActivity, new String[]{permission}, N_PERMISSIONS_REQUEST);
            } else {
                callInterface(runningActivity, permission, checkPermissionGranted(permission));
            }
        }

    }

    /**
     * This method is called in onRequestPermissionsResult of the runningActivity
     *
     * @param requestCode The request code of runningActivity.
     * @param permissions The requested permissions of runningActivity.
     * @param grantResults The grant results for the corresponding permissions from runningActivity
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull
                                                   String[] permissions,
                                           @NonNull
                                                   int[] grantResults) {
        try {
            if (requestCode == N_PERMISSIONS_REQUEST) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (runningActivity != null) {
                        callInterface(runningActivity, permissions[0], true);
                    }
                } else {
                    if (runningActivity != null) {
                        callInterface(runningActivity, permissions[0], false);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to call OnPermissionResult interface
     *
     * @param activity activity reference
     * @param permission current asked permission
     * @param isGranted true if permission granted false otherwise
     * @throws InterfaceNotImplementedException throws when OnPermissionResult is not implemented
     */
    private void callInterface(Activity activity, String permission, boolean isGranted)
            throws InterfaceNotImplementedException {
        Method method;
        try {
            method = activity.getClass().getMethod("onPermissionResult", String.class, boolean.class);
        } catch (NoSuchMethodException e) {
            throw new InterfaceNotImplementedException(
                    "please implement NPermission.OnPermissionResult interface in your activity to get the permissions result");
        }
        try {
            if (isForceRequest() && !isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    requestPermission(activity, permission);
                } else {
                    startInstalledAppDetailsActivity(activity, true);
                }
            } else {
                method.invoke(activity, permission, isGranted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface to notify permission result
     */
    public interface OnPermissionResult {
        /**
         * Method will get called after permission request
         *
         * @param permission asked permission
         * @param isGranted true if permission granted false otherwise
         */
        void onPermissionResult(String permission, boolean isGranted);

    }

    /**
     * Exception throws when OnPermissionResult interface not implemented
     */
    private class InterfaceNotImplementedException extends RuntimeException {
        private InterfaceNotImplementedException(String message) {
            super(message);
        }
    }
}