package tech.yaowen.lib_share.media.video.codec

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.os.Build
import android.util.Log

object Device {
    const val TAG = "MediaCodecInfo"
    fun getSupportMediaCodex(): Array<MediaCodecInfo> {

        val list = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        return list.codecInfos
    }

    fun debugSupportCodec(supportCodes: Array<MediaCodecInfo>) {
        for (codec in supportCodes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.i(
                    TAG,
                    "codex: ${codec.name}, encoder: ${codec.isEncoder}, hw: ${codec.isHardwareAccelerated}, softOnly: ${codec.isSoftwareOnly}, support: ${ codec.supportedTypes.joinToString()}"
                )
            } else {
                Log.i(TAG, "codex: ${codec.name}, encoder: ${codec.isEncoder}, support: ${codec.supportedTypes.joinToString()}")
            }
            val supportTypes = codec.supportedTypes
            for (type in supportTypes) {
                val capability = codec.getCapabilitiesForType(type).videoCapabilities
                if (capability == null) {
                    Log.i(TAG, "support type: $type, null")
                } else {
                    Log.i(TAG, "support type: $type, lower: ${capability.supportedHeights.lower}, upper: ${capability.supportedHeights.upper}")
                }
            }
        }
    }

    fun getMediaCodecList(): IntArray? {
        //获取解码器列表
        val numCodecs = MediaCodecList.getCodecCount()
        var codecInfo: MediaCodecInfo? = null
        var i = 0
        while (i < numCodecs && codecInfo == null) {
            val info = MediaCodecList.getCodecInfoAt(i)
            if (!info.isEncoder) {
                i++
                continue
            }
            val types = info.supportedTypes
            var found = false
            //轮训所要的解码器
            var j = 0
            while (j < types.size && !found) {
                if (types[j] == "video/avc") {
                    found = true
                }
                j++
            }
            if (!found) {
                i++
                continue
            }
            codecInfo = info
            i++
        }
        Log.d(TAG,
            "found" + codecInfo!!.name + "supporting" + " video/avc"
        )
        val capabilities = codecInfo.getCapabilitiesForType("video/avc")
        return capabilities.colorFormats
    }

    fun getMediaCodecList1(): IntArray? {
        //获取解码器列表
        val codecInfo = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos
        var useCodec: MediaCodecInfo? = null
        for (codec in codecInfo) {
            if (!codec.isEncoder) {
                continue
            }
            val types = codec.supportedTypes
            var found = false
            //轮训所要的解码器
            var j = 0
            while (j < types.size && !found) {
                if (types[j] == "video/avc") {
                    found = true
                }
                j++
            }
            if (found) {
                useCodec = codec
                break
            }
        }
        Log.d(TAG,
            "found" + useCodec!!.name + "supporting" + " video/avc"
        )
        val capabilities = useCodec.getCapabilitiesForType("video/avc")
        return capabilities.colorFormats
    }


    fun getOldSupportMediaCodex(): Array<MediaCodecInfo> {
        val numCodecs = MediaCodecList.getCodecCount();
        val codecInfo = Array<MediaCodecInfo>(numCodecs) {
            MediaCodecList.getCodecInfoAt(it)

        }
        return codecInfo
    }
}