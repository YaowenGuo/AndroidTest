package tech.yaowen.customview.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import tech.yaowen.customview.dpToPx

class TextImageView : View {

    companion object {
        const val TAG = "TextImageView"
        const val text =
            "Article Two of the United States Constitution establishes the executive branch of the federal government, which carries out and enforces federal laws. Article Two vests the power of the executive branch in the office of the President of the United States, lays out the procedures for electing and removing the president, and establishes the president's powers and responsibilities.\n" +
                    "Section 1 of Article Two establishes the positions of the president and the vice president, and sets the term of both offices at four years. Section 1's Vesting Clause declares that the executive power of the federal government is vested in the president and, along with the Vesting Clauses of Article One and Article Three, establishes the separation of powers between the three branches of government. Section 1 also establishes the Electoral College, the body charged with electing the president and the vice president. Section 1 provides that each state chooses members of the Electoral College in a manner directed by each state's respective legislature, with the states granted electors equal to their combined representation in both houses of Congress. Section 1 lays out the procedures of the Electoral College and requires the House of Representatives to hold a contingent election to select the president if no individual wins a majority of the electoral vote. Section 1 also sets forth the eligibility requirements for the office of the president, provides procedures in case of a presidential vacancy, and requires the president to take an oath of office.\n" +
                    "Section 2 of Article Two lays out the powers of the presidency, establishing that the president serves as the commander-in-chief of the military and has the power to grant pardons and require the \"principal officer\" of any executive department to tender advice. Though not required by Article Two, President George Washington organized the principal officers of the executive departments into the Cabinet, a practice that subsequent presidents have followed. The Treaty Clause grants the president the power to enter into treaties with the approval of two-thirds of the Senate. The Appointments Clause grants the president the power to appoint judges and public officials subject to the advice and consent of the Senate, which in practice has meant that presidential appointees must be confirmed by a majority vote in the Senate. The Appointments Clause also establishes that Congress can, by law, allow the president, the courts, or the heads of departments to appoint \"inferior officers\" without requiring the advice and consent of the Senate. The final clause of Section 2 grants the president the power to make recess appointments to fill vacancies that occur when the Senate is in recess."
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius = 0f
    private var circleWidth = 20.dpToPx()

    private var textSize = 20.dpToPx()

    val measuredWidth = floatArrayOf(0f)

    @JvmOverloads
    constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
            : super(context, attrSet, defStyleAttr, defStyleRes)

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.textSize = textSize
//        paint.color = Color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = width / 6f * 2
    }

    private var start = 0
    private var count = 0
    private var textBaseLine = 0f
    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            paint.color = Color.GRAY
            paint.style = Paint.Style.FILL_AND_STROKE
            val centerX = width / 2f
            val centerY = height / 2f
            drawCircle(centerX, centerY, radius, paint)

            paint.color = Color.BLACK

            while (start < text.length) {
                textBaseLine += paint.fontSpacing



                if ((textBaseLine + paint.fontMetrics.descent) < (centerY - radius)
                    || textBaseLine + paint.fontMetrics.ascent > (centerY + radius)
                ) {
                    val count = paint.breakText(text, start, text.length, true, width.toFloat(), measuredWidth)
                    drawText(text, start, start + count, 0f, textBaseLine, paint)
                    start += count
                } else {
                    var crossLineY = textBaseLine + paint.fontMetrics.descent // 文字的下边相交线
                    val distanceX = if (crossLineY < centerY) { // 圆心上边的行
                        Math.sqrt(
                            Math.pow(radius.toDouble(), 2.0) - Math.pow((crossLineY - centerY).toDouble(), 2.0)
                        )


                    } else { // 圆心下边的行
                        crossLineY = textBaseLine + paint.fontMetrics.ascent // 文字的上边相交线

                        Math.sqrt(
                            Math.pow(radius.toDouble(), 2.0) - Math.pow((crossLineY - centerY).toDouble(), 2.0)
                        )
                    }
                    val point1X = centerX - distanceX
                    val point2X = centerX + distanceX

                    if (point1X > 0) {
                        val count = paint.breakText(text, start, text.length, true, point1X.toFloat(), measuredWidth)
                        drawText(text, start, start + count, 0f, textBaseLine, paint)
                        start += count
                    }

                    if (point2X < width) {
                        val count = paint.breakText(text, start, text.length, true, (width - point2X).toFloat(), measuredWidth)
                        drawText(text, start, start + count, point2X.toFloat(), textBaseLine, paint)
                        start += count
                    }
                }

            }


        }
    }

    fun pointInCircle(pointX: Float, pointY: Float, circleX: Float, circleY: Float, radius: Float): Boolean {
        return (Math.pow(((circleX - pointX).toDouble()), 2.toDouble()) + Math.pow(
            (circleY - pointY).toDouble(),
            2.toDouble()
        )
                <= Math.pow(radius.toDouble(), 2.toDouble()))
    }
}