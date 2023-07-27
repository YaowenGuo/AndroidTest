package tech.yaowen.customview;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioMix {
    @SuppressLint("WrongConstant")
    public void mix(String videoFile, String audioFile, String outputFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFile);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMilliseconds = Long.parseLong(time);
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MediaExtractor videoExtractor = null;
        MediaExtractor audioExtractor = null;
        MediaMuxer mixMediaMuxer = null;
        try {
            videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(videoFile);
            int videoIndex = -1;
            MediaFormat videoTrackFormat = null;
            int trackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                videoTrackFormat = videoExtractor.getTrackFormat(i);
                if (videoTrackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                    videoIndex = i;
                }
            }

            audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(audioFile);
            int audioIndex = -1;
            MediaFormat audioTrackFormat = null;
            trackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                audioTrackFormat = audioExtractor.getTrackFormat(i);
                if (audioTrackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    audioIndex = i;
                }
            }

            videoExtractor.selectTrack(videoIndex);
            audioExtractor.selectTrack(audioIndex);

            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

            mixMediaMuxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int videoTrackIndex = mixMediaMuxer.addTrack(videoTrackFormat);
            int audioTrackIndex = mixMediaMuxer.addTrack(audioTrackFormat);

            mixMediaMuxer.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
            long videotime;
            long audiotime;

            {
                videoExtractor.readSampleData(byteBuffer, 0);
                if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtractor.advance();
                }
                videoExtractor.readSampleData(byteBuffer, 0);
                long sampleTime = videoExtractor.getSampleTime();
                videoExtractor.advance();
                videoExtractor.readSampleData(byteBuffer, 0);
                long sampleTime1 = videoExtractor.getSampleTime();
                videoExtractor.advance();

                videotime = Math.abs(sampleTime - sampleTime1);
            }

            {
                audioExtractor.readSampleData(byteBuffer, 0);
                if (audioExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    audioExtractor.advance();
                }
                audioExtractor.readSampleData(byteBuffer, 0);
                long sampleTime = audioExtractor.getSampleTime();
                audioExtractor.advance();
                audioExtractor.readSampleData(byteBuffer, 0);
                long sampleTime1 = audioExtractor.getSampleTime();
                audioExtractor.advance();

                audiotime = Math.abs(sampleTime - sampleTime1);
            }

            videoExtractor.unselectTrack(videoIndex);
            videoExtractor.selectTrack(videoIndex);

            while (true) {
                int data = videoExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }
                videoBufferInfo.size = data;
                videoBufferInfo.presentationTimeUs += videotime;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();

                mixMediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }

            while (true) {
                int data = audioExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }
                audioBufferInfo.size = data;
                audioBufferInfo.presentationTimeUs += audiotime;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioExtractor.getSampleFlags();

                mixMediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, audioBufferInfo);
                audioExtractor.advance();
            }
            //Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mixMediaMuxer != null) {
                mixMediaMuxer.stop();
                mixMediaMuxer.release();
            }
            videoExtractor.release();
            audioExtractor.release();
        }
    }
}
