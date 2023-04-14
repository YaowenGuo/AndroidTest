package tech.yaowen.media_player

import android.content.Context
import android.os.Handler
import androidx.media3.common.util.Log
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.video.VideoRendererEventListener
import java.util.ArrayList

//class FilterRenderersFactory(context: Context) : DefaultRenderersFactory(context) {
//    companion object {
//        private const val TAG = "FilterRenderersFactory"
//    }
//
//    override fun buildVideoRenderers(
//        context: Context,
//        extensionRendererMode: Int,
//        mediaCodecSelector: MediaCodecSelector,
//        enableDecoderFallback: Boolean,
//        eventHandler: Handler,
//        eventListener: VideoRendererEventListener,
//        allowedVideoJoiningTimeMs: Long,
//        out: ArrayList<Renderer>
//    ) {
//        val videoRenderer = FilterMediaCodecVideoRenderer(
//            context,
//            codecAdapterFactory,
//            mediaCodecSelector,
//            allowedVideoJoiningTimeMs,
//            enableDecoderFallback,
//            eventHandler,
//            eventListener,
//            MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
//        )
//        out.add(videoRenderer)
//        if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
//            return
//        }
//        var extensionRendererIndex = out.size
//        if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
//            extensionRendererIndex--
//        }
//        try {
//            // Full class names used for constructor args so the LINT rule triggers if any of them move.
//            val clazz = Class.forName("androidx.media3.decoder.vp9.LibvpxVideoRenderer")
//            val constructor = clazz.getConstructor(
//                Long::class.javaPrimitiveType,
//                Handler::class.java,
//                VideoRendererEventListener::class.java,
//                Int::class.javaPrimitiveType
//            )
//            val renderer = constructor.newInstance(
//                allowedVideoJoiningTimeMs,
//                eventHandler,
//                eventListener,
//                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
//            ) as Renderer
//            out.add(extensionRendererIndex++, renderer)
//            Log.i(TAG, "Loaded LibvpxVideoRenderer.")
//        } catch (e: ClassNotFoundException) {
//            // Expected if the app was built without the extension.
//        } catch (e: Exception) {
//            // The extension is present, but instantiation failed.
//            throw RuntimeException("Error instantiating VP9 extension", e)
//        }
//        try {
//            // Full class names used for constructor args so the LINT rule triggers if any of them move.
//            val clazz = Class.forName("androidx.media3.decoder.av1.Libgav1VideoRenderer")
//            val constructor = clazz.getConstructor(
//                Long::class.javaPrimitiveType,
//                Handler::class.java,
//                VideoRendererEventListener::class.java,
//                Int::class.javaPrimitiveType
//            )
//            val renderer = constructor.newInstance(
//                allowedVideoJoiningTimeMs,
//                eventHandler,
//                eventListener,
//                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
//            ) as Renderer
//            out.add(extensionRendererIndex++, renderer)
//            Log.i(TAG, "Loaded Libgav1VideoRenderer.")
//        } catch (e: ClassNotFoundException) {
//            // Expected if the app was built without the extension.
//        } catch (e: Exception) {
//            // The extension is present, but instantiation failed.
//            throw RuntimeException("Error instantiating AV1 extension", e)
//        }
//    }
//}