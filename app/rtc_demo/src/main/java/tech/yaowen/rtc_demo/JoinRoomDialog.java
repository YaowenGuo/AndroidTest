package tech.yaowen.rtc_demo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class JoinRoomDialog extends Dialog {
    private EditText input;
    private Button submit;
    private OnSubmitListener listener;
    public JoinRoomDialog(@NonNull Context context, OnSubmitListener listener) {
        super(context);
        setCancelable(false);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room_dialog);
        input = findViewById(R.id.input);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (input.getText() != null && input.getText().length() > 0) {
                if (listener != null) listener.onSubmit(input.getText().toString());
                dismiss();
            } else {
                Toast.makeText(getContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface OnSubmitListener {
        void onSubmit(String content);
    }
}
