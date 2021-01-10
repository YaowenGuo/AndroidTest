package tech.yaowen.ffmpeg.libavutil;

public class AVUtil {
    static {
        System.loadLibrary("ffmpeg_lib");
    }
    public static native String getFFmpegVersion();
}
