package tech.yaowen.lib_share.widget;

import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import tech.yaowen.lib_share.R;

public class DialogUtil {
    /**
     * 设置 Dialog 从底部弹出。
     *
     * @param dialog 要设置从底部弹出的 Dialog
     */
    public static void makeBottomStyle(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomSheet_Popup);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    /**
     * @param dialog  要设置的 dialog
     * @param gravity #android.view.Gravity 的常亮，如 Gravity.CENTER
     */
    public static void makeDialogGravity(@NonNull Dialog dialog, int gravity) {
        Window window = dialog.getWindow();
        window.setGravity(gravity);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    /**
     * 使 Dialog 后面的 Activity 也能接收 Dialog 内没有处理的事件。
     */

    public static void makeActivityTouchable(@NonNull Dialog dialog, boolean touchable) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();

        if (attributesParams == null) return;
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 失去焦点，从而下一层VIEW获得焦点
        if (touchable) {
            attributesParams.flags = attributesParams.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            attributesParams.flags = attributesParams.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
    }

    public static void backgroundTransparent(@NonNull Dialog dialog, float touchable) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置背景透明度 背景透明
        lp.dimAmount = 0f;
        window.setAttributes(lp);
    }
}
