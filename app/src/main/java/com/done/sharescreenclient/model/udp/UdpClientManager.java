package com.done.sharescreenclient.model.udp;

import com.done.sharescreenclient.constant.Constants;
import com.done.sharescreenclient.model.tcp.TcpSocketCode;
import com.done.sharescreenclient.util.DoneLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

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

public class UdpClientManager {

    private static final String TAG = "UdpClientManager";

    private static UdpClientManager instance;

    public static UdpClientManager getInstance() {
        if (null == instance) {
            synchronized (UdpClientManager.class) {
                if (null == instance) {
                    instance = new UdpClientManager();
                }
            }
        }
        return instance;
    }

    private OnUdpStatus onUdpStatus;

    private StreamClient streamClient;

    public void setOnUdpStatus(OnUdpStatus onUdpStatus) {
        this.onUdpStatus = onUdpStatus;
    }

    public void enableStream() {
        if (streamClient == null) {
            streamClient = new StreamClient();
        } else {
            streamClient.release();
            streamClient = null;
            streamClient = new StreamClient();
        }
        streamClient.start();
    }

    public void release() {
        if (streamClient != null) {
            streamClient.release();
        }
    }

    public void onDestroy() {
        release();
        instance = null;
    }

    private void callbackConn() {
        if (onUdpStatus != null) {
            onUdpStatus.onConnect();
        }
    }

    private void callbackDisconn(int code, String message) {
        if (onUdpStatus != null) {
            onUdpStatus.onDisconnect(code, message);
        }
    }

    private void callbackReceive(byte[] data) {
        if (onUdpStatus != null) {
            onUdpStatus.onReceive(data);
        }
    }

    private class StreamClient extends Thread {

        private static final int FRAME_MAX_NUMBER = 61 * 1024 * 100;
        private byte[] frameBytes = new byte[FRAME_MAX_NUMBER];
        ByteBuffer byteBuffer;

        DatagramSocket client;

        private boolean isRun = true;

        @Override
        public void run() {
            initClient();
            while (isRun) {
                work();
            }
        }

        public void release() {
            isRun = false;
            if (client != null) {
                client.close();
                client = null;
            }
        }

        private void initClient() {
            try {
                byteBuffer = ByteBuffer.allocateDirect(FRAME_MAX_NUMBER * 2);
                client = new DatagramSocket(Constants.RTDP_CLIENT_VIDEO_PORT);
                callbackConn();
                isRun = true;
            } catch (SocketException e) {
                isRun = false;
                e.printStackTrace();
                callbackDisconn(TcpSocketCode.SOCKET_FAILED, "udp connect failed!");
            }
        }

        private void work() {
            DatagramPacket datagramPacket = new DatagramPacket(frameBytes, frameBytes.length);
            while (isRun) {
                try {
                    client.receive(datagramPacket);
                } catch (IOException e) {
                    if (e instanceof SocketException) {
                        callbackDisconn(TcpSocketCode.SOCKET_FAILED, "udp Socket is disconnect!");
                    }
                    e.printStackTrace();
                    DoneLogger.e(TAG, "udp receive failed!");
                }
                byte[] receiveData = datagramPacket.getData();
                DoneLogger.d(TAG, "---------->receive h264:" + receiveData.length + String.format(",%02x,%02x", receiveData[0], receiveData[1]));
                byte[] data = parsePackage(receiveData);
                if (data == null) {
                    continue;
                }
//                byte[] data = datagramPacket.getData();
//                byte[] lengthByte = new byte[4];
//                System.arraycopy(data, 0, lengthByte, 0, 4);
//                int frameLength = TyteUtil.byteArrayToInt(lengthByte);
//                if (frameLength == 0) {
//                    continue;
//                }
////                frameLength = 40960 < frameLength ? 40960 : frameLength;
//                frameLength = FRAME_MAX_NUMBER < frameLength ? FRAME_MAX_NUMBER : frameLength;
//                byte[] frame = new byte[frameLength];
//                System.arraycopy(data, 4, frame, 0, frameLength);
                callbackReceive(data);
            }
        }

        private byte[] parsePackage(byte[] data) {
            int allCount = data[0];
            int curPackage = data[1];
            int realDataLen = data[5] & 0xFF |
                    (data[4] & 0xFF) << 8 |
                    (data[3] & 0xFF) << 16 |
                    (data[2] & 0xFF) << 24;
            byte[] ret = null;
            DoneLogger.d(TAG, "--------------->allCount:" + allCount +
                    ",curPackage:" + curPackage +
                    ",data len:" + realDataLen +
                    ",detail:" + String.format("%02x,%02x,%02x,%02x", data[2], data[3], data[4], data[5]));
            if (allCount == 1 && curPackage == 1) {
                ret = new byte[realDataLen];
                System.arraycopy(data, 6, ret, 0, realDataLen);
                DoneLogger.d(TAG, "--------------->only data");
                return ret;
            }
            if (allCount > 1) {
                if (curPackage == 1) {
                    byteBuffer.clear();
                }
                byteBuffer.put(data, 6, realDataLen);
                if (allCount == curPackage) {
                    int dataLen = byteBuffer.position();
                    ret = new byte[dataLen];
                    byteBuffer.flip();
                    byteBuffer.get(ret);
                    byteBuffer.clear();
                    DoneLogger.d(TAG, "--------------->all data:" + allCount);
                }
            }
            return ret;
        }
    }
}