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
```java
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
        nDefaultPermission.requestPermission(Manifest.permission.CAMERA, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nDefaultPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                if (isGranted) {
                    Log.e("CAMERA", "granted");
                    nDefaultPermission.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    Log.e("CAMERA", "denied");
                    nDefaultPermission.requestPermission(Manifest.permission.CAMERA);
                }
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                if (isGranted) {
                    Log.e("WRITE_EXTERNAL_STORAGE", "granted");
                    nDefaultPermission.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    Log.e("WRITE_EXTERNAL_STORAGE", "denied");
                    nDefaultPermission.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                if (isGranted) {
                    Log.e("READ_EXTERNAL_STORAGE", "granted");
                } else {
                    Log.e("READ_EXTERNAL_STORAGE", "denied");
                    nDefaultPermission.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
        }
    }
}
```
#### Option 2: Just request force all permission must be granted. The permission will be fetched from manifests

```

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
```