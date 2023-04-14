package tech.yaowen.media_player

import android.media.MediaCodecList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

import tech.yaowen.ffmpeg.libavutil.AVUtil
import tech.yaowen.media_player.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.text = "当前 FFmpeg 版本为：" + AVUtil.getFFmpegVersion()

        playVideo(binding)
//        getSupportMediaCodex()
    }

    private fun playVideo(binding: ActivityMainBinding) = binding.apply {
        val renderer = FilterRenderersFactory(this@MainActivity)
//        renderer.setMediaCodecSelector(BlackListMediaCodecSelector(arrayOf("c2.qti.avc.decoder", "omx.qcom.video.decoder", "omx.mtk.video.decoder")))
        val audioPlayer = ExoPlayer.Builder(this@MainActivity)
            .setRenderersFactory(renderer)
            .build()
        playerView.player = audioPlayer

        val uri = Uri.fromFile(File("//android_asset/jkl6xJsiBbcA.mp4"))
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
