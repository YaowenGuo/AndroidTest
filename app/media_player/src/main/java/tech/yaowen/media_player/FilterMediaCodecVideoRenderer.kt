package tech.yaowen.media_player

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaCodecList
import android.os.Build
import android.os.Handler
import android.util.ArrayMap
import android.util.Log
import android.view.Display
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.mediacodec.MediaCodecAdapter
import androidx.media3.exoplayer.mediacodec.MediaCodecInfo
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil.*
import androidx.media3.exoplayer.video.MediaCodecVideoRenderer
import androidx.media3.exoplayer.video.VideoRendererEventListener
import com.google.common.base.Ascii
import com.google.common.collect.ImmutableList

class FilterMediaCodecVideoRenderer(
    val context: Context,
    codecAdapterFactory: MediaCodecAdapter.Factory,
    mediaCodecSelector: MediaCodecSelector,
    allowedJoiningTimeMs: Long,
    enableDecoderFallback: Boolean,
    eventHandler: Handler?,
    eventListener: VideoRendererEventListener?,
    maxDroppedFramesToNotify: Int
) : MediaCodecVideoRenderer(
    context,
    codecAdapterFactory,
    mediaCodecSelector,
    allowedJoiningTimeMs,
    enableDecoderFallback,
    eventHandler,
    eventListener,
    maxDroppedFramesToNotify
) {

    @Throws(DecoderQueryException::class)
    override protected fun getDecoderInfos(
        mediaCodecSelector: MediaCodecSelector,
        format: Format,
        requiresSecureDecoder: Boolean
    ): MutableList<MediaCodecInfo> {
        return MediaCodecUtil.getDecoderInfosSortedByFormatSupport(
            getSupportDecoderInfos(
                context,
                mediaCodecSelector, format, requiresSecureDecoder, configuration.tunneling
            ),
            format
        )
    }


    @Throws(DecoderQueryException::class)
    private fun getSupportDecoderInfos(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        format: Format,
        requiresSecureDecoder: Boolean,
        requiresTunnelingDecoder: Boolean
    ): MutableList<MediaCodecInfo> {
        val mimeType = format.sampleMimeType ?: return ImmutableList.of()
        var decoderInfos: List<MediaCodecInfo> = mediaCodecSelector.getDecoderInfos(
            mimeType, requiresSecureDecoder, requiresTunnelingDecoder
        )

        decoderInfos = filterNotSupportResolutionCodec(format, decoderInfos)

        val alternativeMimeType = MediaCodecUtil.getAlternativeCodecMimeType(format)
            ?: return ImmutableList.copyOf(decoderInfos)
        var alternativeDecoderInfos: List<MediaCodecInfo> = mediaCodecSelector.getDecoderInfos(
            alternativeMimeType, requiresSecureDecoder, requiresTunnelingDecoder
        )
        alternativeDecoderInfos =
            filterNotSupportResolutionCodec(format, alternativeDecoderInfos)

        return if (Util.SDK_INT >= 26 && MimeTypes.VIDEO_DOLBY_VISION == format.sampleMimeType && alternativeDecoderInfos.isNotEmpty()
            && !Api26.doesDisplaySupportDolbyVision(context)
        ) {
            ImmutableList.copyOf(alternativeDecoderInfos)
        } else ImmutableList.builder<MediaCodecInfo?>()
            .addAll(decoderInfos)
            .addAll(alternativeDecoderInfos)
            .build()
    }

    // 过滤掉分辨率不支持的解码器。其它函数都是为了覆盖父类
    private fun filterNotSupportResolutionCodec(
        format: Format,
        codecInfoList: List<MediaCodecInfo>
    ): List<MediaCodecInfo> {
        val logMsgBuilder = StringBuffer()
        logMsgBuilder.append("Format: $format\n")
        val result = codecInfoList.filter {
            logMsgBuilder.append("Codec: ${it.name} mineType ${it.mimeType}, codecMimeType: ${it.codecMimeType}, softwareOnly: ${it.softwareOnly}, hw: ${it.hardwareAccelerated}\n")

            val capability = it.capabilities?.videoCapabilities
            if (capability == null) {
                true
            } else {
                logMsgBuilder.append(
                    "Capabilities: width: ${capability.supportedWidths}, height: ${capability.supportedHeights}, widthAlignment: ${capability.widthAlignment}, heightAlignment: ${capability.heightAlignment}, supportedFrameRates: ${capability.supportedFrameRates}, bitrateRange: ${capability.bitrateRange}\n"
                )

                // 软解码能超出解码范围，不用过滤。
                val include = it.softwareOnly
                        || (capability.supportedHeights.lower <= format.height
                        && format.height <= capability.supportedHeights.upper
                        && capability.supportedWidths.lower <= format.width
                        && format.width <= capability.supportedWidths.upper)
                if (!include) {
                    logMsgBuilder.append("Be filtered out\n")
                }
                include
            }
        }
        Log.e("exo_codec_filter", logMsgBuilder.toString())
        return result
    }

    @RequiresApi(26)
    private object Api26 {
        @DoNotInline
        fun doesDisplaySupportDolbyVision(context: Context): Boolean {
            var supportsDolbyVision = false
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
            if (display != null && display.isHdr) {
                val supportedHdrTypes = display.hdrCapabilities.supportedHdrTypes
                for (hdrType in supportedHdrTypes) {
                    if (hdrType == Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION) {
                        supportsDolbyVision = true
                        break
                    }
                }
            }
            return supportsDolbyVision
        }
    }
}