[![](https://jitpack.io/v/nhancv/nc-android-permission.svg)](https://jitpack.io/#nhancv/nc-android-permission)

## Installation
https://jitpack.io/#nhancv/nc-android-permission/

### Declare permission in Manifest
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
```

### In MainActivity implements OnPermissionResult

#### Option 1: Request permission explicitly in Runtime purpose
Ex
```
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nhancv.npermission.ExplicitPermissionResult
import com.nhancv.npermission.NExplicitPermission


class MainActivity : AppCompatActivity(), ExplicitPermissionResult {

    private var nCameraPermission: NExplicitPermission? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // @nhancv 2019-08-25: Create permission controller
        nCameraPermission = NExplicitPermission(this,
                explicitPermissionResult = this)
    }

    override fun onResume() {
        super.onResume()
        nCameraPermission?.requestPermission(android.Manifest.permission.CAMERA, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        nCameraPermission!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplicitPermissionResult(permission: String, isGranted: Boolean) {
        Log.e(TAG, "onExplicitPermissionResult: $permission isGranted: $isGranted")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
```
#### Option 2: Just request force all permission must be granted. The permission will be fetched from manifests

```

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nhancv.npermission.AllPermissionResult
import com.nhancv.npermission.NAllPermission


class MainActivity : AppCompatActivity(), AllPermissionResult {

    private var nAllPermission: NAllPermission? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // @nhancv 2019-08-25: Create permission controller
        nAllPermission = NAllPermission(this,
                allPermissionResult = this)
    }

    override fun onResume() {
        super.onResume()
        nAllPermission?.forceAllPermissionGranted()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        nAllPermission!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAllPermissionGranted() {
        Log.e(TAG, "onAllPermissionGranted: isAllGranted")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

```

#### Note

Some devices/emulators auto add Google issues tracker permission. To avoid it add snip code below to manifests
```
    <uses-permission
        android:name="com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE"
        tools:node="remove" />
```