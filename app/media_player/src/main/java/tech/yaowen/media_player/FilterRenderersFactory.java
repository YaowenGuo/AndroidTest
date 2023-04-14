package tech.yaowen.media_player;

import android.content.Context;
import android.os.Handler;

import androidx.media3.common.util.Log;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;
import androidx.media3.exoplayer.video.MediaCodecVideoRenderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

// Kotlin 的反射比较慢，暂时使用 Java.
public class FilterRenderersFactory extends DefaultRenderersFactory {
    private static final String TAG = "FilterRenderersFactory";


    public FilterRenderersFactory(Context context) {
        super(context);
    }


    @Override
    protected void buildVideoRenderers(
            Context context,
            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode,
            MediaCodecSelector mediaCodecSelector,
            boolean enableDecoderFallback,
            Handler eventHandler,
            VideoRendererEventListener eventListener,
            long allowedVideoJoiningTimeMs,
            ArrayList<Renderer> out) {
        Log.e("MediaCodecInfo", "buildVideoRenderers");

        MediaCodecVideoRenderer videoRenderer = new FilterMediaCodecVideoRenderer(
                context,
                getCodecAdapterFactory(),
                mediaCodecSelector,
                allowedVideoJoiningTimeMs,
                enableDecoderFallback,
                eventHandler,
                eventListener,
                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
        out.add(videoRenderer);

        if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
            return;
        }
        int extensionRendererIndex = out.size();
        if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
            extensionRendererIndex--;
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            Class<?> clazz = Class.forName("androidx.media3.decoder.vp9.LibvpxVideoRenderer");
            Constructor<?> constructor = clazz.getConstructor(long.class, android.os.Handler.class,
                    androidx.media3.exoplayer.video.VideoRendererEventListener.class, int.class);
            Renderer renderer = (Renderer) constructor.newInstance(allowedVideoJoiningTimeMs,
                    eventHandler, eventListener, MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
            out.add(extensionRendererIndex++, renderer);
            Log.i(TAG, "Loaded LibvpxVideoRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating VP9 extension", e);
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            Class<?> clazz = Class.forName("androidx.media3.decoder.av1.Libgav1VideoRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            long.class,
                            android.os.Handler.class,
                            androidx.media3.exoplayer.video.VideoRendererEventListener.class,
                            int.class);
            Renderer renderer =
                    (Renderer)
                            constructor.newInstance(
                                    allowedVideoJoiningTimeMs,
                                    eventHandler,
                                    eventListener,
                                    MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
            out.add(extensionRendererIndex++, renderer);
            Log.i(TAG, "Loaded Libgav1VideoRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating AV1 extension", e);
        }
    }
}
