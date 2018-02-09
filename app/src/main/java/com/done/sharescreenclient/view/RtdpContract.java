package com.done.sharescreenclient.view;

import com.done.sharescreenclient.base.BasePresenter;
import com.done.sharescreenclient.base.BaseView;
import com.done.sharescreenclient.model.RequestEntity;
import com.done.sharescreenclient.model.ResponseEntity;

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

public interface RtdpContract {

    interface ILiveView extends BaseView<Presenter> {

        void onConnecting();

        void onConnectedFailed();

        void onConnected();

        void onDisconnected();

        void onReceiveStream(byte[] h264);

        void onHOME(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onBACK(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onMENU(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onVOLUME(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onSETUP(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onPLAY(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onTEARDOWN(RequestEntity requestEntity, ResponseEntity responseEntity);

        void onHEART(RequestEntity requestEntity, ResponseEntity responseEntity);
    }

    interface Presenter extends BasePresenter {

        void requestHome();

        void requestBack();

        void requestMenu();

        /**
         * if value>-1 the method mean set device volume
         *
         * @param isAdd if true is up volume else low
         * @param value volume set
         * @param type  {@link com.done.sharescreenclient.constant.Constants#RTDP_REQUEST_CONTENT_VOLUME_TYPE_MUSIC}
         */
        void requestVolume(boolean isAdd, int value, String type);

        void requestSetup();

        void requestPlay();

        void requestTeardown();

        /**
         * control window click
         * calculate form{@link MySurfaceView}
         *
         * @param src        display view's source params' width and height
         * @param clickModel click coordinate from view
         */
        void requestClick(ClickModel src, ClickModel clickModel);

        /**
         * control window touch
         * calculate form{@link MySurfaceView}
         *
         * @param src        display view's source params' width and height
         * @param touchModel touch coordinate from view and include start and end
         */
        void requestTouch(ClickModel src, TouchModel touchModel);

        void requestHeart();
    }
}
