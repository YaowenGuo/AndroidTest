package tech.yaowen.customview.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import tech.yaowen.customview.R;

public class TestDialogActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dialog);
        findViewById(R.id.content).setOnClickListener(v -> {
            Toast.makeText(this, "Activity", Toast.LENGTH_SHORT)
                    .show();
        });
        button = findViewById(R.id.content);
        button.setText(System.getProperty("java.vm.name"));
        new ActivityClickableDialog(this, false, null)
                .show();

    }

}