[![](https://jitpack.io/v/nhancv/nc-android-permission.svg)](https://jitpack.io/#nhancv/nc-android-permission)

Installation
https://jitpack.io/#nhancv/nc-android-permission/

Declare permission in Manifest
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

In MainActivity implements NPermission.OnPermissionResult

Ex
```java
public class MainActivity extends AppCompatActivity implements NPermission.OnPermissionResult {

    private NPermission nPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //@nhancv TODO: request camera permission
        nPermission = new NPermission(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
```
