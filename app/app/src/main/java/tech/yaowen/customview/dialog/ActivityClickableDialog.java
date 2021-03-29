package tech.yaowen.customview.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import tech.yaowen.customview.R;
import tech.yaowen.lib_share.widget.DialogUtil;

public class ActivityClickableDialog extends Dialog {
    View button;
    protected ActivityClickableDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickable_dialog);
        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Dialog", Toast.LENGTH_SHORT)
                            .show();
                        dialogDismissAnim(true);
                });

//        enableBackClickable();
        DialogUtil.backgroundTransparent(this, 0);
        DialogUtil.makeActivityTouchable(this, true);
    }

    void enableBackClickable() {
        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();

        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 失去焦点，从而下一层VIEW获得焦点
        attributesParams.flags = attributesParams.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        attributesParams.flags = attributesParams.flags & (~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


    void enableBackClickableOld() {
        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();

        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 失去焦点，从而下一层VIEW获得焦点
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        attributesParams.dimAmount = 0.4f; //设置遮罩透明度

//        int width = (int) (ScreenUtils.getScreenWidth() * 0.8f); // 设置Dailog 宽度

//        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void dialogDismissAnim(boolean little) {

        float endScale = 0.4f;

//        int endWidth = (int) (rootView.getWidth() * endScale);
        // 故意用的 rootView.getWidth()， rootView.getHeight() 获得的值不准确（大约是宽的两倍），很奇怪。
//        int endHeight = (int) (rootView.getWidth() * endScale);
//        int screenW = Size.getScreenWidth();
//        int screenH = ScreenUtils.getScreenHeight();
//        int moveDistanceX = ((screenW - endWidth) >> 1) + SizeUtils.dp2px(20);
//        int moveDistanceY = (screenH - endHeight) >> 1;
//        ValueAnimator animator = ObjectAnimator.ofInt(drawable, "alpha", 0x4c, 0);
//        animator.start();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        button.animate()
                .scaleX(endScale)
                .scaleY(endScale)
                .translationX(-200)
                .translationY(500)
               /* .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
//                        lp.gravity = lp.gravity & ~(Gravity.CLIP_HORIZONTAL | Gravity.CLIP_VERTICAL);
                        dialogWindow.setAttributes(lp);
//                        if (dialogWindow.getDecorView() instanceof ViewGroup) {
////                            dialogWindow.setClipToOutline(false);
//                            ViewGroup root = ((ViewGroup) dialogWindow.getDecorView());
//                            ViewGroup rooot = (ViewGroup)dialogWindow.getDecorView().getRootView();
//                            ((ViewGroup) dialogWindow.getDecorView()).setClipChildren(false);
//
//                        }
                    }
                })*/
                .start();


        //设置背景透明度 背景透明
//        lp.dimAmount = 0f;
        ValueAnimator animator = ObjectAnimator.ofFloat(0.4f, 0);
        animator.addUpdateListener(animation -> {
            lp.dimAmount = (float)animation.getAnimatedValue();
            dialogWindow.setAttributes(lp);
        });
        if (little) {

        } else {
            animator.reverse();
        }
        animator.start();

        dialogWindow.setAttributes(lp);
//        makeActivityTouchable(this, little);
    }
}
