package com.done.sharescreenclient.model.tcp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.done.sharescreenclient.constant.Constants;
import com.done.sharescreenclient.model.OnResponseListener;
import com.done.sharescreenclient.model.RequestEntity;
import com.done.sharescreenclient.model.ResponseEntity;
import com.done.sharescreenclient.util.DoneLogger;
import com.done.sharescreenclient.util.RtdpProtocolUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

public class TcpClientManager {

    private static final String TAG = "TcpClientManager";

    private static TcpClientManager instance;

    private ThreadPoolExecutor poolExecutor;


    private static final int CORE_POOL_SIZE = 3;
    /**
     * 线程池最多同时执行8个线程
     */
    private static final int MAX_POOL_SIZE = 3;
    /**
     * 线程池中线程在空闲时，存留时间
     */
    private static final int KEEP_ALIVE_TIME = 1;

    private BlockingQueue<Runnable> taskQueue;

    public static TcpClientManager getInstance() {
        if (instance == null) {
            synchronized (TcpClientManager.class) {
                if (instance == null) {
                    instance = new TcpClientManager();
                }
            }
        }
        return instance;
    }

    private TcpClientManager() {
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        ThreadFactory threadFactory = new ThreadFactory() {

            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, TAG + mCount.getAndIncrement());
            }
        };
        taskQueue = new LinkedBlockingQueue<>();
        poolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                timeUnit,
                taskQueue,
                threadFactory);
    }

    private TcpSender tcpSender;
    private TcpConnector tcpConnector;
    private TcpReceiver tcpReceiver;

    public void send(String data) {
        if (tcpSender == null) {
            tcpSender = new TcpSender();
        }
        tcpSender.setData(data);
        poolExecutor.execute(tcpSender);
        receive();
    }

    public void connect() {
        if (tcpConnector == null) {
            tcpConnector = new TcpConnector();
        } else {
            release();
        }
        Future<Boolean> result = poolExecutor.submit(tcpConnector);
//        try {
//            DoneLogger.d(TAG, "client connect result:" + result.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    private void receive() {
        if (tcpReceiver == null) {
            tcpReceiver = new TcpReceiver();
        }
        poolExecutor.execute(tcpReceiver);
    }


    private Socket client;

    private BufferedReader in;

    private OutputStream out;

    private OnTcpStatus onTcpStatus;

    private OnResponseListener onResponseListener;

    public void setOnTcpStatus(OnTcpStatus onTcpStatus) {
        this.onTcpStatus = onTcpStatus;
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    /**
     * The method release some resources for socket, if u invoke this method,  u should reconnect
     */
    public void release() {
        if (in != null) {
            try {
                in.close();
                in = null;
                tcpReceiver = null;
            } catch (IOException e) {
            }
        }
        if (out != null) {
            try {
                out.close();
                out = null;
                tcpSender = null;
            } catch (IOException e) {
            }
        }
        if (client != null) {
            try {
                client.close();
                client = null;
                tcpConnector = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The method destroy all include {@link #instance} and {@link #poolExecutor}
     */
    public void onDestroy() {
        release();
        poolExecutor.shutdown();
        poolExecutor = null;
        instance = null;
    }

    private void callbackConn() {
        if (onTcpStatus != null) {
            onTcpStatus.onConnected();
        }
    }

    private void callbackDisconn(int code, String message) {
        if (onTcpStatus != null) {
            onTcpStatus.onDisconnected(code, message);
        }
    }

    private void callbackResponseSuccess(RequestEntity requestEntity, ResponseEntity responseEntity) {
        if (onResponseListener != null) {
            onResponseListener.onSuccess(requestEntity, responseEntity);
        }
    }

    private void callbackResponseFailed(int code, String message, Object data) {
        if (onResponseListener != null) {
            onResponseListener.onFailed(code, message, data);
        }
    }

    private class TcpConnector implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            client = new Socket();
            try {
                client.connect(new InetSocketAddress(Constants.RTDP_SERVER_IP, Constants.RTDP_SERVER_CONTROL_PORT),
                        Constants.READ_TIMEOUT);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = client.getOutputStream();
                callbackConn();
                return true;
            } catch (SocketTimeoutException ste) {
                ste.printStackTrace();
                callbackDisconn(TcpSocketCode.SOCKET_TIMEOUT, "connect server timeout!");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                DoneLogger.e(TAG, "connect server failed!");
                callbackDisconn(TcpSocketCode.SOCKET_FAILED, "connect server failed!");
                return false;
            }
        }
    }

    private class TcpSender implements Runnable {

        private String data;

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            if (!TextUtils.isEmpty(data)) {
                try {
                    out.write(data.getBytes());
                    out.flush();
                    DoneLogger.i(TAG, "Client -> Server:" + data);
                } catch (IOException e) {
                    DoneLogger.e(TAG, "发送服务端失败");
                    callbackResponseFailed(Constants.REQUEST_CODE_ERROR, "client send data failed!", RtdpProtocolUtils.processRequest(data));
                    e.printStackTrace();
                }
            }
        }
    }

    private class TcpReceiver implements Runnable {

        @Override
        public void run() {
            try {
                client.setSoTimeout(Constants.READ_TIMEOUT);
            } catch (SocketException e) {
                DoneLogger.e(TAG, "set read timeout is error!");
                callbackResponseFailed(Constants.REQUEST_CODE_ERROR, "set read timeout is error!", RtdpProtocolUtils.processRequest(tcpSender.data));
                e.printStackTrace();
                return;
            }
            String line;
            try {
                line = in.readLine();
                if (line == null) {
                    callbackDisconn(TcpSocketCode.SOCKET_FAILED, "client is disconnect!");
                } else {
                    DoneLogger.i(TAG, "client is receive:" + line);
                    handleReceive(line);
                }
            } catch (SocketTimeoutException ste) {
                DoneLogger.e(TAG, "client read timeout!");
                callbackDisconn(Constants.RESPONSE_CODE_TIMEOUT, "client is receive timeout!");
                ste.printStackTrace();
            } catch (IOException e) {
                DoneLogger.e(TAG, "client receive error!");
                callbackDisconn(Constants.RESPONSE_CODE_ERROR, "client is receive timeout!");
                e.printStackTrace();
            }
        }

        private void handleReceive(String line) {
            String[] fields = line.split(Constants.SPACE);
            ResponseEntity responseEntity = null;
            if (fields.length == Constants.RESPONSE_FIELD_COUNT) {
                responseEntity = new ResponseEntity();
                responseEntity.status = fields[0];
                responseEntity.cseq = fields[1];
                responseEntity.length = fields[2];
                responseEntity.content = fields[3];
                callbackResponseSuccess(RtdpProtocolUtils.processRequest(tcpSender.data), responseEntity);
            }
        }
    }

}
