package tech.yaowen.arm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("arm");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Object o = new Object();
        testHWASan();
    }

    native void testCAS();

    native void testHWASan();
}