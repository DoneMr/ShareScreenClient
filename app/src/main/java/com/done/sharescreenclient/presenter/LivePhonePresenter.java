package com.done.sharescreenclient.presenter;

import com.done.sharescreenclient.MyApplication;
import com.done.sharescreenclient.constant.Constants;
import com.done.sharescreenclient.model.ControlStreamer;
import com.done.sharescreenclient.model.IControlStream;
import com.done.sharescreenclient.model.OnResponseListener;
import com.done.sharescreenclient.model.RequestEntity;
import com.done.sharescreenclient.model.ResponseEntity;
import com.done.sharescreenclient.model.tcp.OnTcpStatus;
import com.done.sharescreenclient.model.tcp.TcpSocketCode;
import com.done.sharescreenclient.model.udp.OnUdpStatus;
import com.done.sharescreenclient.util.DoneLogger;
import com.done.sharescreenclient.util.RtdpProtocolUtils;
import com.done.sharescreenclient.util.ToastUtils;
import com.done.sharescreenclient.view.ClickModel;
import com.done.sharescreenclient.view.RtdpContract;
import com.done.sharescreenclient.view.TouchModel;

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
 * Created by Done on 2017/12/12.
 *
 * @author by Done
 */

public class LivePhonePresenter implements RtdpContract.Presenter {

    private static final String TAG = "LivePhonePresenter";

    private RtdpContract.ILiveView liveView;

    private ControlStreamer controlStreamer;

    private boolean isTcpConnect = false;
    private boolean isUdpConnect = false;
    private ResponseListener responseListener;
    private TcpStatusListener tcpStatusListener;
    private UdpStatusListener udpStatusListener;

    public LivePhonePresenter(RtdpContract.ILiveView liveView) {
        this.liveView = liveView;
        responseListener = new ResponseListener();
        tcpStatusListener = new TcpStatusListener();
        udpStatusListener = new UdpStatusListener();
        controlStreamer = new ControlStreamer();
        controlStreamer.setResponseListener(responseListener);
        controlStreamer.setTcpStatusListener(tcpStatusListener);
        controlStreamer.setUdpStatusListener(udpStatusListener);
    }

    public void connect() {
        liveView.onConnecting();
        connectTcp();
        connectUdp();
    }

    public void disconnect() {
        disconnectTcp();
        disconnectUdp();
    }

    private void connectTcp() {
        controlStreamer.connectTcp();
    }

    private void connectUdp() {
        controlStreamer.connectUdp();
    }

    private void disconnectTcp() {
        isTcpConnect = false;
        liveView.onDisconnected();
        controlStreamer.disconnectTcp();
    }

    private void disconnectUdp() {
        controlStreamer.disconnectUdp();
    }

    public void release() {
        controlStreamer.destroy();
    }

    @Override
    public void start() {
        liveView.setPresenter(this);
        connect();
    }

    private void send(String data) {
        controlStreamer.send(data);
    }

    @Override
    public void requestHome() {
        RequestEntity requestEntity = RtdpProtocolUtils.processHOMERequest();
        request(requestEntity);
    }

    @Override
    public void requestBack() {
        RequestEntity requestEntity = RtdpProtocolUtils.processBACKRequest();
        request(requestEntity);
    }

    @Override
    public void requestMenu() {
        RequestEntity requestEntity = RtdpProtocolUtils.processMENURequest();
        request(requestEntity);
    }

    @Override
    public void requestVolume(boolean isAdd, int value, String type) {
        RequestEntity requestEntity = RtdpProtocolUtils.processVOLUMERequest(isAdd, value, type);
        request(requestEntity);
    }

    @Override
    public void requestSetup() {
        RequestEntity requestEntity = RtdpProtocolUtils.processSETUPRequest(Constants.RTDP_CLIENT_VIDEO_PORT);
        request(requestEntity);
    }


    @Override
    public void requestPlay() {
        RequestEntity requestEntity = RtdpProtocolUtils.processPLAYRequest(Constants.RTDP_REQUEST_CONTENT_PLAY);
        request(requestEntity);
    }

    @Override
    public void requestTeardown() {
        RequestEntity requestEntity = RtdpProtocolUtils.processTEARDOWNRequest(Constants.RTDP_REQUEST_CONTENT_TEARDOWN);
        request(requestEntity);
    }

    @Override
    public void requestClick(ClickModel src, ClickModel clickModel) {

    }

    @Override
    public void requestTouch(ClickModel src, TouchModel touchModel) {

    }

    @Override
    public void requestHeart() {

    }


    private void request(RequestEntity requestEntity) {
        send(requestEntity.toString());
    }

    public class ResponseListener implements OnResponseListener {

        @Override
        public void onSuccess(RequestEntity requestEntity, ResponseEntity responseEntity) {
            DoneLogger.d(TAG, "ResponseListener onSuccess");
            callbackView(requestEntity, responseEntity);
        }

        private void callbackView(RequestEntity requestEntity, ResponseEntity responseEntity) {
            switch (requestEntity.method) {
                case Constants.RTDP_METHOD_HOME:
                    liveView.onHOME(requestEntity, responseEntity);
                    break;
                case Constants.RTDP_METHOD_MENU:
                    liveView.onMENU(requestEntity, responseEntity);
                    break;
                case Constants.RTDP_METHOD_BACK:
                    liveView.onBACK(requestEntity, responseEntity);
                    break;
                case Constants.RTDP_METHOD_VOLUME:
                    liveView.onVOLUME(requestEntity, responseEntity);
                    break;
                case Constants.RTDP_METHOD_TEARDOWN:
                    liveView.onTEARDOWN(requestEntity, responseEntity);
                    break;
                case Constants.RTDP_METHOD_PLAY:
                    liveView.onPLAY(requestEntity, responseEntity);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFailed(int code, String message, Object data) {
            DoneLogger.e(TAG, "ResponseListener onFailed:" + data.toString());

        }
    }

    public class TcpStatusListener implements OnTcpStatus {

        @Override
        public void onConnected() {
            DoneLogger.d(TAG, "TcpStatusListener onConnected");
            isTcpConnect = true;
            if (isUdpConnect) {
                liveView.onConnected();
            }
        }

        @Override
        public void onDisconnected(int code, String message) {
            DoneLogger.d(TAG, "TcpStatusListener onDisconnected");
            isTcpConnect = false;
            if (TcpSocketCode.SOCKET_FAILED == code) {
                liveView.onConnectedFailed();
                ToastUtils.getInstance().showToast("连接失败", MyApplication.gCONTEXT, true);
            } else if (TcpSocketCode.SOCKET_TIMEOUT == code) {
                liveView.onConnectedFailed();
                ToastUtils.getInstance().showToast("连接超时", MyApplication.gCONTEXT, true);
            } else {
                liveView.onDisconnected();
            }
        }
    }

    public class UdpStatusListener implements OnUdpStatus {

        @Override
        public void onConnect() {
            DoneLogger.d(TAG, "UdpStatusListener onConnect");
            isUdpConnect = true;
            if (isTcpConnect) {
                liveView.onConnected();
            }
        }

        @Override
        public void onDisconnect(int code, String message) {
            DoneLogger.d(TAG, "UdpStatusListener onDisconnect");
            isUdpConnect = false;
            liveView.onDisconnected();
        }

        @Override
        public void onReceive(byte[] data) {
//            DoneLogger.d(TAG, "UdpStatusListener onReceive");
            liveView.onReceiveStream(data);
        }
    }
}
