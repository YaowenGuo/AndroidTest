package tech.yaowen.lib_share.widget

import android.app.Dialog
import android.view.Gravity
import android.view.WindowManager
import tech.yaowen.lib_share.R

public object DialogUtil {
    /**
     * 设置 Dialog 从底部弹出。
     *
     * @param dialog 要设置从底部弹出的 Dialog
     */
    fun makeBottomStyle(dialog: Dialog) {
        val window = dialog.window
        window!!.setWindowAnimations(R.style.BottomSheet_Popup)
        window.setGravity(Gravity.BOTTOM)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    /**
     * @param dialog  要设置的 dialog
     * @param gravity #android.view.Gravity 的常亮，如 Gravity.CENTER
     */
    fun makeDialogGravity(dialog: Dialog, gravity: Int) {
        val window = dialog.window
        window!!.setGravity(gravity)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    /**
     * 使 Dialog 后面的 Activity 也能接收 Dialog 内没有处理的事件。
     */
    @JvmStatic
    fun makeActivityTouchable(dialog: Dialog, touchable: Boolean) {
        val window = dialog.window
        val attributesParams = window!!.attributes ?: return
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 失去焦点，从而下一层VIEW获得焦点
        if (touchable) {
            attributesParams.flags =
                attributesParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            attributesParams.flags =
                attributesParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        }
    }

    @JvmStatic
    fun backgroundTransparent(dialog: Dialog, touchable: Float) {
        val window = dialog.window
        val lp = window!!.attributes
        //设置背景透明度 背景透明
        lp.dimAmount = 0f
        window.attributes = lp
    }
}