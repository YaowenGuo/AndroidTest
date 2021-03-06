package tech.yaowen.media_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadHelper.createMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import kotlinx.android.synthetic.main.activity_main.view.*
import tech.yaowen.ffmpeg.libavutil.AVUtil
import tech.yaowen.media_player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var player: SimpleExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SimpleExoPlayer.Builder(this)
        // Build the video MediaSource.
//        val videoSource = ProgressiveMediaSource.Factory(...)
//        .createMediaSource('');
        val binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.text = "当前 FFmpeg 版本为：" + AVUtil.getFFmpegVersion()

    }
}
