package com.done.sharescreenclient.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.done.sharescreenclient.constant.Constants;
import com.done.sharescreenclient.model.RequestEntity;
import com.done.sharescreenclient.view.ClickModel;
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
 * Created by Done on 2017/12/5.
 *
 * @author by Done
 */

public class RtdpProtocolUtils {

    private static int cseq = 0;

    public synchronized static RequestEntity processHOMERequest() {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_HOME,
                Constants.RTDP_REQUEST_CONTENT_HOME);
        return ret;
    }

    public synchronized static RequestEntity processBACKRequest() {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_BACK,
                Constants.RTDP_REQUEST_CONTENT_BACK);
        return ret;
    }

    public synchronized static RequestEntity processMENURequest() {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_MENU,
                Constants.RTDP_REQUEST_CONTENT_MENU);
        return ret;
    }

    public synchronized static RequestEntity processVOLUMERequest(boolean isAdd, int value, String type) {
        RequestEntity ret;
        if (value > -1) {
            ret = processRequest(Constants.RTDP_METHOD_VOLUME,
                    Constants.RTDP_REQUEST_CONTENT_VOLUME_SET +
                            value +
                            Constants.RTDP_REQUEST_CONTENT_SPLIT +
                            Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE +
                            type);
        } else {
            if (isAdd) {
                ret = processRequest(Constants.RTDP_METHOD_VOLUME,
                        Constants.RTDP_REQUEST_CONTENT_VOLUME_SET +
                                Constants.RTDP_REQUEST_CONTENT_VOLUME_SET_UP +
                                Constants.RTDP_REQUEST_CONTENT_SPLIT +
                                Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE +
                                type);
            } else {
                ret = processRequest(Constants.RTDP_METHOD_VOLUME,
                        Constants.RTDP_REQUEST_CONTENT_VOLUME_SET +
                                Constants.RTDP_REQUEST_CONTENT_VOLUME_SET_LOW +
                                Constants.RTDP_REQUEST_CONTENT_SPLIT +
                                Constants.RTDP_REQUEST_CONTENT_VOLUME_TYPE +
                                type);
            }
        }
        return ret;
    }

    public synchronized static RequestEntity processSETUPRequest(@NonNull int port) {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_SETUP,
                Constants.RTDP_REQUEST_CONTENT_SETUP + port);
        return ret;
    }

    public synchronized static RequestEntity processPLAYRequest(@NonNull String play) {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_PLAY, play);
        return ret;
    }

    public synchronized static RequestEntity processTEARDOWNRequest(@NonNull String shutdown) {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_TEARDOWN, shutdown);
        return ret;
    }

    public synchronized static RequestEntity processCLICKRequest(@NonNull ClickModel src, @NonNull ClickModel clickModel) {
        //TODO
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_CLICK,
                Constants.RTDP_REQUEST_CONTENT_CLICK_SRC);
        return ret;
    }

    public synchronized static RequestEntity processTOUCHRequest(@NonNull ClickModel src, @NonNull TouchModel touchModel) {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_TOUCH,
                Constants.RTDP_REQUEST_CONTENT_CLICK_SRC);
        return ret;
    }

    public synchronized static RequestEntity processHEARTRequest(@NonNull String heart) {
        RequestEntity ret = processRequest(Constants.RTDP_METHOD_SETUP, heart);
        return ret;
    }

    public synchronized static RequestEntity processRequest(@NonNull String src) {
        String[] contents = src.split(Constants.SPACE);
        if (contents.length == Constants.REQUEST_FIELD_COUNT) {
            RequestEntity requestEntity = new RequestEntity();
            requestEntity.method = contents[0];
            requestEntity.cseq = contents[1];
            requestEntity.length = contents[2];
            requestEntity.content = contents[3];
            return requestEntity;
        } else {
            return null;
        }
    }

    private synchronized static RequestEntity processRequest(String method, String content) {
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.method = method;
        requestEntity.cseq = String.valueOf(getCSeq());
        requestEntity.length = getLength(content);
        requestEntity.content = content;
        return requestEntity;
    }

    /**
     * calculate content length
     *
     * @param content request content
     * @return
     */
    private synchronized static String getLength(String content) {
        int ret = 0;
        if (!TextUtils.isEmpty(content)) {
            ret = content.length();
        }
        return String.valueOf(ret);
    }

    private synchronized static int getCSeq() {
        return ++cseq;
    }
}
