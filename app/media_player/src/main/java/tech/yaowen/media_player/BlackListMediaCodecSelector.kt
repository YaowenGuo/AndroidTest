package tech.yaowen.media_player

import android.util.Log
import androidx.media3.exoplayer.mediacodec.MediaCodecInfo
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil.DecoderQueryException

class BlackListMediaCodecSelector internal constructor(private val BLACKLISTEDCODECS: Array<String>) :
    MediaCodecSelector {
    @Throws(DecoderQueryException::class)
    override fun getDecoderInfos(
        mimeType: String,
        requiresSecureDecoder: Boolean,
        requiresTunnelingDecoder: Boolean
    ): List<MediaCodecInfo> {
        val codecInfoList = MediaCodecUtil.getDecoderInfos(
            mimeType,
            requiresSecureDecoder,
            requiresTunnelingDecoder
        )

        // filter codecs based on blacklist template
        val filteredCodecInfo: MutableList<MediaCodecInfo> = ArrayList()
        for (codecInfo in codecInfoList) {
            if (!blacklistedCodec(codecInfo.name.lowercase())) {
                filteredCodecInfo.add(codecInfo)
            }
        }
        return filteredCodecInfo
    }

    private fun blacklistedCodec(codecName: String): Boolean {
        Log.e("MediaCodec", codecName)
        for (blackListedCodec in BLACKLISTEDCODECS) {
            if (codecName.contains(blackListedCodec)) {
                return true
            }
        }
        return false
    }
}