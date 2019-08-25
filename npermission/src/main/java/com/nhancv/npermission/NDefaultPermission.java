/*
 * Developed by Nhan Cao on 8/25/19 9:44 AM.
 * Last modified 8/25/19 9:41 AM.
 * Copyright (c) 2019. All rights reserved.
 */

package com.nhancv.npermission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by nhancao on 1/12/17.
 */

public class NDefaultPermission implements NPermission {
    private static final String TAG = NDefaultPermission.class.getSimpleName();
    // Request code for all permissions
    private final int N_PERMISSIONS_REQUEST = 0x007;
    // Activity reference for reflection
    private Activity runningActivity;
    // All needed permission
    private List<String> allNeededPermissions;
    // This flag for force request permission
    private boolean isPersistent;
    // This flag detect user select nerver ask in force request mode
    private boolean neverAskFlag;
    // This flag present that show Toast in App Setting to guide user enable permission manually
    private boolean showToastInSetting;

    public NDefaultPermission(final Activity runningActivity) {
        this(runningActivity, true);
    }

    public NDefaultPermission(final Activity runningActivity, boolean showToastInSetting) {
        this.runningActivity = runningActivity;
        this.showToastInSetting = showToastInSetting;

        allNeededPermissions = new ArrayList<>();
        refreshNeedPermissions();
    }

    /**
     * Refresh needed permissions
     */
    private void refreshNeedPermissions() {
        allNeededPermissions.clear();
        List<String> allPermissions = getRequiredPermissions();
        for (String permission : allPermissions) {
            if (!checkPermissionGranted(permission)) {
                allNeededPermissions.add(permission);
            }
        }
    }

    /**
     * Go to application setting
     */
    public void startInstalledAppDetailsActivity(final boolean showToast) {
        if (runningActivity == null) {
            return;
        }
        neverAskFlag = true;
        if (showToast) {
            Toast.makeText(runningActivity, runningActivity.getResources().getText(R.string.enable_yourself), Toast.LENGTH_SHORT).show();
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + runningActivity.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        runningActivity.startActivity(i);
    }

    public boolean checkPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(runningActivity.getApplicationContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public List<String> getRequiredPermissions() {
        try {
            if (runningActivity != null) {
                PackageInfo info = runningActivity.getPackageManager().getPackageInfo(
                        runningActivity.getPackageName(),
                        PackageManager.GET_PERMISSIONS
                );
                String[] requestedPermissions = info.requestedPermissions;
                if (requestedPermissions != null && requestedPermissions.length > 0) {
                    List<String> myStringList = new ArrayList<>(requestedPermissions.length);
                    myStringList.addAll(Arrays.asList(requestedPermissions));
                    return myStringList;
                }
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Method to call OnPermissionResult interface
     *
     * @param permission current asked permission
     * @param isGranted true if permission granted false otherwise
     * @throws InterfaceNotImplementedException throws when OnPermissionResult is not implemented
     */
    private void callInterface(String permission, boolean isGranted)
            throws InterfaceNotImplementedException {
        if (runningActivity == null) {
            return;
        }

        Method method;
        try {
            method = runningActivity.getClass().getMethod("onPermissionResult", String.class, boolean.class);
        } catch (NoSuchMethodException e) {
            throw new InterfaceNotImplementedException(
                    "Please implement OnPermissionResult interface in your activity to get the permissions result");
        }
        try {
            if (isPersistent && !isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (runningActivity.shouldShowRequestPermissionRationale(permission)) {
                    requestPermission(permission, isPersistent);
                } else {
                    startInstalledAppDetailsActivity(showToastInSetting);
                }
            } else {
                method.invoke(runningActivity, permission, isGranted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull
                                                   String[] permissions,
                                           @NonNull
                                                   int[] grantResults) {
        try {
            if (requestCode == N_PERMISSIONS_REQUEST) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshNeedPermissions();
                    callInterface(permissions[0], true);
                } else {
                    callInterface(permissions[0], false);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forceAllPermissionGranted() {
        if (allNeededPermissions.size() > 0) {
            requestPermission(allNeededPermissions.get(0));
        }
    }

    @Override
    public void requestPermission(@NotNull String permission) {
        requestPermission(permission, true);
    }

    @Override
    public void requestPermission(@NotNull String permission, boolean persistent) {
        isPersistent = persistent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permissionGranted = checkPermissionGranted(permission);
            if (!permissionGranted && !neverAskFlag) {
                ActivityCompat.requestPermissions(runningActivity, new String[]{permission}, N_PERMISSIONS_REQUEST);
            } else {
                callInterface(permission, permissionGranted);
            }
        }
    }

    @NotNull
    @Override
    public List<String> getGetAllNeededPermissions() {
        return allNeededPermissions;
    }

    @Override
    public void setGetAllNeededPermissions(@NotNull List<String> list) {
        allNeededPermissions = list;
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