package tech.yaowen.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imgFile = assets.open("image.jpg")
        val img = BitmapFactory.decodeStream(imgFile)
        val coverFile = assets.open("cover.png")
        val cover = BitmapFactory.decodeStream(coverFile)
        val config = cover.config ?: Bitmap.Config.ARGB_8888
        val bitmap = Bitmap.createBitmap(cover.width, cover.height, config)

        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        val scale = min(608f / img.height, 960f / img.height)
        matrix.setTranslate(((cover.width - img.width * scale).toInt() shr 1).toFloat(),
            ((cover.height - img.height * scale).toInt() shr 1).toFloat()
        )
        matrix.preScale(scale, scale)

        canvas.drawBitmap(img, matrix, null)
        canvas.drawBitmap(cover, 0f, 0f, null)

        setContent {
            Column(Modifier.background(Color.Gray)) {
                BasicText(text = "Hello, world!")
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "some useful description",
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                )

            }
        }
    }
}
