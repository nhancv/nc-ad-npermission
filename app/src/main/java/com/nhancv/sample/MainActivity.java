package com.nhancv.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nhancv.npermission.NPermission;


public class MainActivity extends AppCompatActivity implements NPermission.OnPermissionResult {

    private NPermission nPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //@nhancv TODO: request camera permission
        nPermission = new NPermission(true);
        nPermission.requestPermission(MainActivity.this, Manifest.permission.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                if (isGranted) {
                    Log.e("CAMERA", "granted");
                    nPermission.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    Log.e("CAMERA", "denied");
                    nPermission.requestPermission(this, Manifest.permission.CAMERA);
                }
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                if (isGranted) {
                    Log.e("WRITE_EXTERNAL_STORAGE", "granted");
                    nPermission.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    Log.e("WRITE_EXTERNAL_STORAGE", "denied");
                    nPermission.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                if (isGranted) {
                    Log.e("READ_EXTERNAL_STORAGE", "granted");
                    //@nhancv TODO:
                } else {
                    Log.e("READ_EXTERNAL_STORAGE", "denied");
                    nPermission.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
        }
    }
}
