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

// 消除不同像素密度上，英寸的差别
fun Float.inchWithDp(): Float {
    return this * Resources.getSystem().displayMetrics.density
}



fun Float.dp2Inch(): Float {
    return this.dpToPx() / 72
}

fun Float.inch2Dp(): Float {
    return (this * 72).pxToDp()
}

fun Float.px2Inch(): Float {
    return this / 72
}

fun Float.inch2Px(): Float {
    return this * 72
}