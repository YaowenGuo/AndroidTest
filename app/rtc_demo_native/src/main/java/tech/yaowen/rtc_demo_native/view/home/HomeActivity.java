package tech.yaowen.rtc_demo_native.view.home;

import android.Manifest;
import android.app.NativeActivity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class HomeActivity extends NativeActivity {

    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().post(() -> {
            requestCamera();
        });
    }

    void requestCamera() {
        String[] accessPermissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        boolean needRequire  = false;
        for(String access : accessPermissions) {
            int curPermission = ActivityCompat.checkSelfPermission(this, access);
            if(curPermission != PackageManager.PERMISSION_GRANTED) {
                needRequire = true;
                break;
            }
        }
        if (needRequire) {
            ActivityCompat.requestPermissions(this, accessPermissions, PERMISSION_REQUEST_CODE_CAMERA);
        } else {
            permissionResult(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // if any permission failed, the sample could not play
        if (PERMISSION_REQUEST_CODE_CAMERA != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        permissionResult(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED);
    }

    native static void permissionResult(boolean granted);
    static {
        System.loadLibrary("rtc_demo");
    }
}
