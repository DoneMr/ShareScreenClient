package com.done.sharescreenclient.model;

import android.support.annotation.NonNull;

import com.done.sharescreenclient.model.tcp.OnTcpStatus;
import com.done.sharescreenclient.model.tcp.TcpClientManager;
import com.done.sharescreenclient.model.udp.OnUdpStatus;
import com.done.sharescreenclient.model.udp.UdpClientManager;
import com.done.sharescreenclient.presenter.LivePhonePresenter;
import com.done.sharescreenclient.util.DoneLogger;

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

public class ControlStreamer {

    private static final String TAG = "ControlStreamer";

    private TcpClientManager tcpClientManager;
    private UdpClientManager udpClientManager;

    public ControlStreamer() {
        tcpClientManager = TcpClientManager.getInstance();
        udpClientManager = UdpClientManager.getInstance();
    }

    public void setResponseListener(@NonNull LivePhonePresenter.ResponseListener responseListener) {
        tcpClientManager.setOnResponseListener(responseListener);
    }

    public void setTcpStatusListener(@NonNull LivePhonePresenter.TcpStatusListener tcpStatusListener) {
        tcpClientManager.setOnTcpStatus(tcpStatusListener);
    }

    public void setUdpStatusListener(@NonNull LivePhonePresenter.UdpStatusListener udpStatusListener) {
        udpClientManager.setOnUdpStatus(udpStatusListener);
    }


    public void connectTcp() {
        tcpClientManager.connect();
    }

    public void disconnectTcp() {
        tcpClientManager.release();
    }

    public void connectUdp() {
        udpClientManager.enableStream();
    }

    public void disconnectUdp() {
        udpClientManager.release();
    }

    public void send(String data) {
        tcpClientManager.send(data);
    }

    public void destroy() {
        tcpClientManager.onDestroy();
        udpClientManager.onDestroy();
    }

}
