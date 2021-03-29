package tech.yaowen.customview.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import tech.yaowen.customview.R;

public class TestDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dialog);
        findViewById(R.id.content).setOnClickListener(v -> {
            Toast.makeText(this, "Activity", Toast.LENGTH_SHORT)
                    .show();
        });

        new ActivityClickableDialog(this, false, null)
                .show();

    }

}