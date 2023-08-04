package tech.yaowen.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mp4file = assets.open("cook-live-96.mp4")
        val mp3file = assets.open("cook_live_bg_musitc1.mp3")

        val imgFile = assets.open("image.jpg")
        val img = BitmapFactory.decodeStream(imgFile)
        val coverFile = assets.open("cover.png")
        val cover = BitmapFactory.decodeStream(coverFile)

        val bitmap = Bitmap.createBitmap(cover.width, cover.height, cover.config)

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
            Column(modifier = Modifier.fillMaxWidth().background(Color.Gray)) {
                Text("Hello world!")
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "some useful description",
                )
            }
        }
    }
}
