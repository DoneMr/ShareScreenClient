package com.done.sharescreenclient.fragment;

import android.app.ProgressDialog;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.done.sharescreenclient.MyApplication;
import com.done.sharescreenclient.R;
import com.done.sharescreenclient.constant.Constants;
import com.done.sharescreenclient.model.RequestEntity;
import com.done.sharescreenclient.model.ResponseEntity;
import com.done.sharescreenclient.presenter.LivePhonePresenter;
import com.done.sharescreenclient.util.CodeTool;
import com.done.sharescreenclient.util.DoneLogger;
import com.done.sharescreenclient.util.ToastUtils;
import com.done.sharescreenclient.view.ClickModel;
import com.done.sharescreenclient.view.MySurfaceView;
import com.done.sharescreenclient.view.RtdpContract;
import com.done.sharescreenclient.view.TouchModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
 * Created by Done on 2017/12/11.
 *
 * @author by Done
 */

public class LiveViewFragment extends Fragment implements RtdpContract.ILiveView,
        View.OnClickListener, SurfaceHolder.Callback, Toolbar.OnMenuItemClickListener,
        MySurfaceView.OnControlEvent {

    private static final String TAG = "LiveViewFragment";

    private Button btnServerStatus, btnConnect, btnDisconnect, btnSetup, btnPlay, btnTeardown;
    private Button btnBack, btnHome, btnMenu, btnVolumeUp, btnVolumeLow;
    private Toolbar tbVolume;
    private ToggleButton tbConnect;

    private MySurfaceView sfvPhoneLive;
    private MediaCodec mediaCodec;
    int width = 720;
    int height = 1280;
    private int mCount = 0;


    private LivePhonePresenter presenter;
    private static final int STATUS_CONNECTED = 0;
    private static final int STATUS_DISCONNECTED = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        initView(view);
        presenter = new LivePhonePresenter(this);
        presenter.start();
        return view;
    }

    public void onFrame(byte[] buf) {
        if (buf == null || mediaCodec == null) {
            return;
        }
        int length = buf.length;
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();//拿到输入缓冲区,用于传送数据进行编码
        //返回一个填充了有效数据的input buffer的索引，如果没有可用的buffer则返回-1.当timeoutUs==0时，该方法立即返回；当timeoutUs<0时，无限期地等待一个可用的input buffer;当timeoutUs>0时，至多等待timeoutUs微妙
//        int inputBufferIndex = mediaCodec.dequeueInputBuffer(1);// =>0时,至多等待x微妙   如果发送源快速滑动比如放视频, 花屏明显.. ...
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);//    <-1时,无限期地等待一个可用的input buffer  会出现:一直等待导致加载异常, 甚至会吃掉网络通道, 没有任何异常出现...(调试中大部分是因为sps和pps没有写入到解码器, 保证图像信息的参数写入解码器很重要)
        if (inputBufferIndex >= 0) {//当输入缓冲区有效时,就是>=0
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            int decodeLen = inputBuffer.limit();
            if (length > decodeLen) {
                return;
            }
            DoneLogger.d(TAG, "待解码的数据大小:" + length);
            inputBuffer.put(buf, 0, length);//往输入缓冲区写入数据,关键点
            int value = buf[4] & 0x0f;//nalu, 5是I帧, 7是sps 8是pps.
            if (value == 7) {//如果不能保证第一帧写入的是sps和pps， 可以用这种方式等待sps和pps发送到之后写入解码器
                DoneLogger.e(TAG, "更新视频的sps," + "系统提供输入buffer大小:" + decodeLen);

                mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * 30, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);//更新sps和pps
            } else if (value == 5) {
                DoneLogger.d(TAG, "关键帧");
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * 30, 0);//将缓冲区入队
            } else {
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * 30, 0);//将缓冲区入队
            }

            mCount++;//用于queueInputBuffer presentationTimeUs 此缓冲区的显示时间戳（以微秒为单位），通常是这个缓冲区应该呈现的媒体时间
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);//拿到输出缓冲区的索引
//        Log.e(TAG, "outputBufferIndex" + outputBufferIndex);
        while (outputBufferIndex >= 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);//显示并释放资源
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);//再次获取数据，如果没有数据输出则outIndex=-1 循环结束
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    private void initView(View view) {
        if (view != null) {
            tbVolume = view.findViewById(R.id.tb_volume);
            tbVolume.inflateMenu(R.menu.volume);
            tbVolume.getMenu().getItem(0).setChecked(true);
            tbVolume.setOnMenuItemClickListener(this);
            tbConnect = view.findViewById(R.id.tb_connect);
            tbConnect.setOnClickListener(this);
            btnServerStatus = view.findViewById(R.id.btn_server_status);
            btnConnect = view.findViewById(R.id.btn_connect);
            btnDisconnect = view.findViewById(R.id.btn_disconnect);
            btnBack = view.findViewById(R.id.btn_back);
            btnHome = view.findViewById(R.id.btn_home);
            btnMenu = view.findViewById(R.id.btn_menu);
            btnSetup = view.findViewById(R.id.btn_setup);
            btnPlay = view.findViewById(R.id.btn_play);
            btnTeardown = view.findViewById(R.id.btn_teardown);
            sfvPhoneLive = view.findViewById(R.id.sfv_phone);
            btnVolumeUp = view.findViewById(R.id.btn_volume_up);
            btnVolumeLow = view.findViewById(R.id.btn_volume_low);
            sfvPhoneLive.getHolder().addCallback(this);
            sfvPhoneLive.setOnControlEvent(this);
            btnVolumeUp.setOnClickListener(this);
            btnVolumeLow.setOnClickListener(this);
            btnSetup.setOnClickListener(this);
            btnPlay.setOnClickListener(this);
            btnServerStatus.setOnClickListener(this);
            btnTeardown.setOnClickListener(this);
            btnConnect.setOnClickListener(this);
            btnDisconnect.setOnClickListener(this);
            btnBack.setOnClickListener(this);
            btnHome.setOnClickListener(this);
            btnMenu.setOnClickListener(this);
            btnServerStatus.setEnabled(false);
            btnServerStatus.setBackgroundColor(getContext().getResources().getColor(R.color.red));
            btnDisconnect.setEnabled(false);
            setKeyEnable(false);
            setTcpEnable(false);
        }
    }

    private void setTcpEnable(boolean isEnable) {
        btnSetup.setEnabled(isEnable);
        btnPlay.setEnabled(isEnable);
        btnTeardown.setEnabled(isEnable);
    }

    private void setKeyEnable(boolean isEnable) {
        MyApplication.gHANDLER.post(() -> {
            btnBack.setEnabled(isEnable);
            btnHome.setEnabled(isEnable);
            btnMenu.setEnabled(isEnable);
            btnVolumeLow.setEnabled(isEnable);
            btnVolumeUp.setEnabled(isEnable);
        });
    }

    private void showToast(String message) {
        ToastUtils.getInstance().showToast(message, getContext(), true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_connect:
                if (tbConnect.isChecked()) {
                    presenter.connect();
                } else {
                    presenter.disconnect();
                }
                break;
            case R.id.btn_connect:
                presenter.connect();
                break;
            case R.id.btn_disconnect:
                presenter.disconnect();
                break;
            case R.id.btn_setup:
                presenter.requestSetup();
                break;
            case R.id.btn_play:
                presenter.requestPlay();
                break;
            case R.id.btn_teardown:
                presenter.requestTeardown();
                break;
            case R.id.btn_back:
                presenter.requestBack();
                break;
            case R.id.btn_home:
                presenter.requestHome();
                break;
            case R.id.btn_menu:
                presenter.requestMenu();
                break;
            case R.id.btn_volume_up:
                presenter.requestVolume(true, -1, getVolumeType());
                break;
            case R.id.btn_volume_low:
                presenter.requestVolume(false, -1, getVolumeType());
                break;
            default:
                break;
        }
    }

    @Override
    public void setPresenter(RtdpContract.Presenter presenter) {

    }

    @Override
    public void onConnecting() {
        refreshUi(showDialog(), 0);
    }

    @Override
    public void onConnectedFailed() {
        updateUi(STATUS_DISCONNECTED);
        refreshUi(dismissDialog(), 1000);
    }

    @Override
    public void onConnected() {
        updateUi(STATUS_CONNECTED);
        refreshUi(dismissDialog(), 1000);
    }

    @Override
    public void onDisconnected() {
        updateUi(STATUS_DISCONNECTED);
        refreshUi(dismissDialog(), 1000);
    }

    @Override
    public void onReceiveStream(byte[] h264) {
        onFrame(h264);
    }

    @Override
    public void onHOME(RequestEntity requestEntity, ResponseEntity responseEntity) {
        handleResponseKey("onHOME", requestEntity, responseEntity);
    }


    @Override
    public void onBACK(RequestEntity requestEntity, ResponseEntity responseEntity) {
        handleResponseKey("onBACK", requestEntity, responseEntity);

    }

    @Override
    public void onMENU(RequestEntity requestEntity, ResponseEntity responseEntity) {
        handleResponseKey("onMENU", requestEntity, responseEntity);

    }

    @Override
    public void onVOLUME(RequestEntity requestEntity, ResponseEntity responseEntity) {

    }

    private void handleResponseKey(String methodKey, RequestEntity requestEntity, ResponseEntity responseEntity) {
        String showStr = methodKey + " Failed!";
        if (requestEntity.cseq.equals(responseEntity.cseq)) {
            if (Constants.RESPONSE_CODE_CONNECT_SUCCESS == Integer.parseInt(responseEntity.status)) {
                showStr = methodKey + " Success";
            } else {
                showStr += "STATUS is ERROR!" + responseEntity.status;
            }
        } else {
            showStr += "CSEQ is ERROR!";
        }
        showToast(showStr);
    }

    @Override
    public void onSETUP(RequestEntity requestEntity, ResponseEntity responseEntity) {

    }

    @Override
    public void onPLAY(RequestEntity requestEntity, ResponseEntity responseEntity) {
        setKeyEnable(true);
    }

    @Override
    public void onTEARDOWN(RequestEntity requestEntity, ResponseEntity responseEntity) {
        setKeyEnable(false);
    }

    @Override
    public void onHEART(RequestEntity requestEntity, ResponseEntity responseEntity) {

    }


    private void refreshUi(Runnable runnable, long delayed) {
        if (delayed > 0) {
            MyApplication.gHANDLER.postDelayed(runnable, delayed);
        } else {
            MyApplication.gHANDLER.post(runnable);
        }
    }

    private ProgressDialog progressDialog;

    private Runnable showDialog() {
        return () -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage("Connecting...");
            progressDialog.show();
        };
    }

    private Runnable dismissDialog() {
        return () -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        };
    }


    private void updateUi(int status) {
        MyApplication.gHANDLER.post(() -> {
            switch (status) {
                case STATUS_CONNECTED:
                    btnServerStatus.setBackgroundColor(getContext().getResources().getColor(R.color.green));
                    btnDisconnect.setEnabled(true);
                    btnConnect.setEnabled(false);
                    setKeyEnable(false);
                    setTcpEnable(true);
                    tbConnect.setChecked(true);
                    break;
                case STATUS_DISCONNECTED:
                    btnServerStatus.setBackgroundColor(getContext().getResources().getColor(R.color.red));
                    btnDisconnect.setEnabled(false);
                    btnConnect.setEnabled(true);
                    setKeyEnable(false);
                    setTcpEnable(false);
                    tbConnect.setChecked(false);
                    break;
                default:
                    break;
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createMediaCodec(holder);
    }

    private void createMediaCodec(SurfaceHolder holder) {
        try {
            //通过多媒体格式名创建一个可用的解码器
            mediaCodec = MediaCodec.createDecoderByType("video/avc");
            width = holder.getSurfaceFrame().width();
            height = holder.getSurfaceFrame().height();
        } catch (IOException e) {
            DoneLogger.e(TAG, "通过多媒体格式名创建一个可用的解码器" + e.toString());
            e.printStackTrace();
        }
        //初始化编码器
        final MediaFormat mediaformat = MediaFormat.createVideoFormat("video/avc", width, height);
        //这里可以尝试写死SPS和PPS，部分机型上的解码器可以正常工作。
//        byte[] header_sps = {0, 0, 0, 1, 103, 66, 0, 42, (byte) 149, (byte) 168, 30, 0, (byte) 137, (byte) 249, 102, (byte) 224, 32, 32, 32, 64};
//        byte[] header_pps = {0, 0, 0, 1, 104, (byte) 206, 60, (byte) 128, 0, 0, 0, 1, 6, (byte) 229, 1, (byte) 151, (byte) 128};
//        mediaformat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//        mediaformat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        int frameRate = 20;
        mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        //指定解码后的帧格式
        mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);//解码器将编码的帧解码为这种指定的格式,YUV420Flexible是几乎所有解码器都支持的
        //设置配置参数，参数介绍 ：
        // format   如果为解码器，此处表示输入数据的格式；如果为编码器，此处表示输出数据的格式。
        //surface   指定一个surface，可用作decode的输出渲染。
        //crypto    如果需要给媒体数据加密，此处指定一个crypto类.
        //   flags  如果正在配置的对象是用作编码器，此处加上CONFIGURE_FLAG_ENCODE 标签。
        mediaCodec.configure(mediaformat, holder.getSurface(), null, 0);
        mediaCodec.start();
        DoneLogger.d(TAG, "创建解码器");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        DoneLogger.e(TAG, "surfaceDestroyed");
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec = null;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.volume_alarm:
                showToast("设置音量类型为alarm");
                break;
            case R.id.volume_call:
                showToast("设置音量类型为call");
                break;
            case R.id.volume_music:
                showToast("设置音量类型为music");
                showToast("volume_music");
                break;
            case R.id.volume_notification:
                showToast("设置音量类型为notification");
                break;
            case R.id.volume_ring:
                showToast("设置音量类型为ring");
                break;
            case R.id.volume_system:
                showToast("设置音量类型为system");
                break;
            default:
                break;
        }
        return true;
    }

    private String getVolumeType() {
        if (tbVolume.getMenu().findItem(R.id.volume_alarm).isChecked()) {
            return Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE_ALARM;
        }
        if (tbVolume.getMenu().findItem(R.id.volume_call).isChecked()) {
            return Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE_CALL;
        }
        if (tbVolume.getMenu().findItem(R.id.volume_music).isChecked()) {
            return Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE_MUSIC;
        }
        if (tbVolume.getMenu().findItem(R.id.volume_notification).isChecked()) {
            return Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE_NOTIFICATION;
        }
        if (tbVolume.getMenu().findItem(R.id.volume_ring).isChecked()) {
            return Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE_RING;
        }
        if (tbVolume.getMenu().findItem(R.id.volume_system).isChecked()) {
            return Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE_SYSTEM;
        }
        return "";
    }

    @Override
    public void onClick(ClickModel src, ClickModel clickModel) {
        DoneLogger.d(TAG, "user click view:" + clickModel.X + "," + clickModel.Y
                + "\nsrc:" + src.X + "," + src.Y);
    }

    @Override
    public void onTouch(ClickModel src, TouchModel touchModel) {
        DoneLogger.d(TAG, "user touch view start click:" + touchModel.startClick.toString()
                + "\nsrc:" + src.X + "," + src.Y);

    }

}
