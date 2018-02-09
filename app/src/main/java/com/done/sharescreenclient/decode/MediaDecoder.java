package com.done.sharescreenclient.decode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.SurfaceHolder;

import com.done.sharescreenclient.util.DoneLogger;

import java.io.IOException;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 *
 * @author Done
 * @date 2018/1/11
 */

public class MediaDecoder {

    private static final String TAG = "MediaDecoder";

    private MediaCodec mediaCodec;

    private int mWidth = 720;

    private int mHeight = 1280;

    public MediaDecoder(SurfaceHolder holder) {
        try {
            //通过多媒体格式名创建一个可用的解码器
            mediaCodec = MediaCodec.createDecoderByType("video/avc");
            mWidth = holder.getSurfaceFrame().width();
            mHeight = holder.getSurfaceFrame().height();
        } catch (IOException e) {
            DoneLogger.e(TAG, "通过多媒体格式名创建一个可用的解码器" + e.toString());
            e.printStackTrace();
        }
        //初始化编码器
        final MediaFormat mediaformat = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
        int frameRate = 5;
        mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        //指定解码后的帧格式
        //解码器将编码的帧解码为这种指定的格式,YUV420Flexible是几乎所有解码器都支持的
        mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        //设置配置参数，参数介绍 ：
        // format   如果为解码器，此处表示输入数据的格式；如果为编码器，此处表示输出数据的格式。
        //surface   指定一个surface，可用作decode的输出渲染。
        //crypto    如果需要给媒体数据加密，此处指定一个crypto类.
        //   flags  如果正在配置的对象是用作编码器，此处加上CONFIGURE_FLAG_ENCODE 标签。
        mediaCodec.configure(mediaformat, holder.getSurface(), null, 0);
        DoneLogger.d(TAG, "创建解码器");
    }

    public void startDecode() {
        if (mediaCodec != null) {
            mediaCodec.start();
        }
    }

    public void stopDecode() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
    }
}
