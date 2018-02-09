package com.done.sharescreenclient.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

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
 *
 * @author Done
 * @date 2017/12/15
 */

public class MySurfaceView extends SurfaceView {

    private static final String TAG = "MySurfaceView";

    private int width;
    private int height;

    private OnControlEvent onControlEvent;

    private static final int OFFSET_X = 20;
    private static final int OFFSET_Y = 20;

    public void setOnControlEvent(OnControlEvent onControlEvent) {
        this.onControlEvent = onControlEvent;
    }

    private TouchCalculator touchCalculator;

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touchCalculator = new TouchCalculator();
        setOnTouchListener(touchCalculator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureWidth(widthMeasureSpec);
        height = measureHeight(heightMeasureSpec);
        DoneLogger.i(TAG, "surface width:" + width + ", height:" + height);
        setMeasuredDimension(width, height);
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            DoneLogger.d(TAG, "MeasureSpec.AT_MOST");
        }
        return height;
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            DoneLogger.d(TAG, "MeasureSpec.AT_MOST");
        }
        return height;
    }

    private void callbackClick(ClickModel clickModel) {
        if (onControlEvent != null) {
            ClickModel src = new ClickModel(width, height);
            onControlEvent.onClick(src, clickModel);
        }
    }

    private void callbackTouch(TouchModel touchModel) {
        if (onControlEvent != null) {
            ClickModel src = new ClickModel(width, height);
            onControlEvent.onTouch(src, touchModel);
        }
    }

    private class TouchCalculator implements OnTouchListener {

        private float startX = 0;
        private float startY = 0;
        private float endX = 0;
        private float endY = 0;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    endX = motionEvent.getX();
                    endY = motionEvent.getY();

                    float sumX = Math.abs(endX - startX);
                    float sumY = Math.abs(endY - startY);
                    if (sumX < OFFSET_X && sumY < OFFSET_Y) {
                        ClickModel clickModel = new ClickModel(endX, endY);
                        callbackClick(clickModel);
                    } else {
                        ClickModel startClick = new ClickModel(startX, startY);
                        ClickModel endClick = new ClickModel(endX, endY);
                        TouchModel touchModel = new TouchModel(startClick, endClick);
                        callbackTouch(touchModel);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    public interface OnControlEvent {
        /**
         * click listener
         *
         * @param clickModel click coordinate
         */
        void onClick(ClickModel src, ClickModel clickModel);

        /**
         * touch listener
         *
         * @param touchModel touch coordinate
         */
        void onTouch(ClickModel src, TouchModel touchModel);
    }
}
