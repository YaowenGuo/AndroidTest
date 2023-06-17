package tech.yaowen.lib_share.media.video.display

import android.media.MediaCodecList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

import java.io.File

class ExoPlayerActivity : AppCompatActivity() {
    lateinit var playerView: PlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerView = PlayerView(this)
        setContentView(playerView)
        playVideo()
//        getSupportMediaCodex()
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

    private fun getSupportMediaCodex() {

        val list = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val supportCodes = list.codecInfos
        for (codec in supportCodes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.i(
                    "MediaCodecInfo",
                    "codex: ${codec.name}, encoder: ${codec.isEncoder}, hw: ${codec.isHardwareAccelerated}, softOnly: ${codec.isSoftwareOnly}, support: ${ codec.supportedTypes.joinToString()}"
                )
            } else {
                Log.i("MediaCodecInfo", "codex: ${codec.name}, encoder: ${codec.isEncoder}, support: ${codec.supportedTypes.joinToString()}")
            }
            val supportTypes = codec.supportedTypes
            for (type in supportTypes) {
                val capability = codec.getCapabilitiesForType(type).videoCapabilities
                if (capability == null) {
                    Log.i("MediaCodecInfo", "support type: $type, null")
                } else {
                    Log.i("MediaCodecInfo", "support type: $type, lower: ${capability.supportedHeights.lower}, upper: ${capability.supportedHeights.upper}")
                }
            }
        }
    }
}
