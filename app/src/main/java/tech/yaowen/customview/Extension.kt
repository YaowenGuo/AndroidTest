package tech.yaowen.customview

import android.content.res.Resources
import android.util.TypedValue

fun Int.dpToPx(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
}


fun Int.pxToDp(): Float {
    return this / Resources.getSystem().displayMetrics.density
}

fun Float.dpToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
}

fun Float.pxToDp(): Float {
    return this / Resources.getSystem().displayMetrics.density
}