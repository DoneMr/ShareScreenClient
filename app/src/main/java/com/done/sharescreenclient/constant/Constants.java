package com.done.sharescreenclient.constant;

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
 * Created by Done on 2017/11/23.
 *
 * @author by Done
 */

public class Constants {

    public static final int READ_TIMEOUT = 10 * 1000;

    /**
     * tcp exception codes
     */
    public static final int RESPONSE_CODE_CONNECT_SUCCESS = 200;
    public static final int RESPONSE_CODE_TIMEOUT = 101;
    public static final int RESPONSE_CODE_DISCONNECT = 102;
    public static final int RESPONSE_CODE_PARSE_ERROR = 103;
    public static final int RESPONSE_CODE_ERROR = 104;

    public static final int REQUEST_CODE_ERROR = 105;


    public static final String RESPONSE_CODE_CONNECT_ERROR = "201";
    public static final String RESPONSE_CODE_CONNECT_TIMEOUT = "202";

    /**
     * check params
     */
    public static final int RESPONSE_FIELD_COUNT = 4;
    public static final int REQUEST_FIELD_COUNT = 5;

    /**
     * RTDP　Server configuration
     */
    public static final String RTDP_SERVER_IP = "192.168.43.1";
    public static final int RTDP_SERVER_CONTROL_PORT = 8199;
    public static final int RTDP_SERVER_VIDEO_PORT = 8198;
    public static final int RTDP_CLIENT_VIDEO_PORT = 8085;

    /**
     * rtdp request methods
     */
    public static final String RTDP_METHOD_SETUP = "SETUP";
    public static final String RTDP_METHOD_PLAY = "PLAY";
    public static final String RTDP_METHOD_TEARDOWN = "TEARDOWN";
    public static final String RTDP_METHOD_HEART = "HEART";
    public static final String RTDP_METHOD_HOME = "HOME";
    public static final String RTDP_METHOD_BACK = "BACK";
    public static final String RTDP_METHOD_MENU = "MENU";
    public static final String RTDP_METHOD_VOLUME = "VOLUME";
    public static final String RTDP_METHOD_CLICK = "CLICK";
    public static final String RTDP_METHOD_TOUCH = "TOUCH";

    /**
     * rtdp request contents
     */
    public static final String RTDP_REQUEST_CONTENT_SETUP = "client-port=";
    public static final String RTDP_REQUEST_CONTENT_PLAY = "play";
    public static final String RTDP_REQUEST_CONTENT_TEARDOWN = "shutdown";
    public static final String RTDP_REQUEST_CONTENT_HOME = "click";
    public static final String RTDP_REQUEST_CONTENT_BACK = "click";
    public static final String RTDP_REQUEST_CONTENT_MENU = "click";
    public static final String RTDP_REQUEST_CONTENT_CLICK_SRC = "src=";
    public static final String RTDP_REQUEST_CONTENT_CLICK_ORDER = "order=";
    public static final String RTDP_REQUEST_CONTENT_TOUCH_ORDER = "order=";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_SET = "set=";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE = "type=";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE_CALL = "call";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE_SYSTEM = "system";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE_RING = "ring";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE_MUSIC = "music";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE_ALARM = "alarm";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_TYPE_NOTIFICATION = "notification";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_SET_UP = "up";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_SET_LOW = "low";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_SET_MAX = "max";
    public static final String RTDP_REQUEST_CONTENT_VOLUME_SET_MIN = "min";
    public static final String RTDP_REQUEST_CONTENT_SPLIT = ";";

    public static final String SPACE = " ";
    public static final String CRLF = "\r\n";
}
