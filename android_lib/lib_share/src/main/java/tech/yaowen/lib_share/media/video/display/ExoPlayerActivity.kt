package tech.yaowen.lib_share.media.video.display

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import tech.yaowen.lib_share.media.video.codec.Device.debugSupportCodec
import tech.yaowen.lib_share.media.video.codec.Device.getMediaCodecList
import tech.yaowen.lib_share.media.video.codec.Device.getMediaCodecList1
import tech.yaowen.lib_share.media.video.codec.Device.getOldSupportMediaCodex
import tech.yaowen.lib_share.media.video.codec.Device.getSupportMediaCodex
import java.io.File

class ExoPlayerActivity : AppCompatActivity() {
    lateinit var playerView: PlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerView = PlayerView(this)
        setContentView(playerView)
        playVideo()
        getMediaCodecList()
        getMediaCodecList1()
        debugSupportCodec(getSupportMediaCodex())
        Log.e("MediaCodecInfo", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        debugSupportCodec(getOldSupportMediaCodex())

    }

    private fun playVideo() {
        val renderer = DefaultRenderersFactory(this@ExoPlayerActivity)
        renderer.setEnableDecoderFallback(true)
        val audioPlayer = ExoPlayer.Builder(this@ExoPlayerActivity)
            .setRenderersFactory(renderer)
            .build()
        playerView.player = audioPlayer

        val uri = Uri.fromFile(File("//android_asset/test.mp4"))
        val mediaItem = MediaItem.fromUri(uri)
        audioPlayer.videoDecoderCounters
        audioPlayer.setMediaItem(mediaItem)
        audioPlayer.prepare()
        audioPlayer.playWhenReady = true
        audioPlayer.play()
    }


}
