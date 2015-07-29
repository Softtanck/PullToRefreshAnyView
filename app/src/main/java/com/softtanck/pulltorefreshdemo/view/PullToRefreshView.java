package com.softtanck.pulltorefreshdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.softtanck.pulltorefreshdemo.R;

/**
 * @author : Tanck
 * @Description : TODO 可以扩展刷任意View
 * @date 7/27/2015
 */
public class PullToRefreshView extends LinearLayout {
    private View mChildView;
    private boolean mIsRefreshing;
    private float mTouchStartY;
    private float mTouchCurY;
    private ViewGroup mHeader;
    private int mHeaderHeight;

    private float DEFAULT_VALUE = 0.3f;
    private float mDetalY;
    private CircleView circleView;
    private OnRefreshListener listener;
    private boolean isFlag;

    public interface OnRefreshListener {
        /**
         * 刷新CallBack
         */
        void OnRefreshing();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(final Context context) {
        if (getChildCount() > 1) {
            throw new RuntimeException("you can only attach one child");
        }
        this.post(new Runnable() {
            @Override
            public void run() {
                mChildView = getChildAt(0);
                addHeaderView(context);
            }
        });
    }

    private void addHeaderView(Context context) {
        mHeader = (ViewGroup) View.inflate(context, R.layout.header, null);
        circleView = (CircleView) mHeader.getChildAt(0);
        measureView(mHeader); // measureChild
        mHeaderHeight = mHeader.getMeasuredHeight();
        mHeader.setPadding(0, -mHeaderHeight, 0, 0);
        mHeader.invalidate();
        addView(mHeader, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartY = ev.getY();
                mTouchCurY = mTouchStartY;
                break;
            case MotionEvent.ACTION_MOVE:
                float curY = ev.getY();
                float dy = curY - mTouchStartY;
                if (mIsRefreshing && !canChildScrollUp() || dy > 0 && !canChildScrollUp() || dy < 0 && ((ListView) mChildView).getCount() == ((ListView) mChildView).getLastVisiblePosition() + 1) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mTouchCurY = event.getY();
                mDetalY = (mTouchCurY - mTouchStartY) * DEFAULT_VALUE;
                if (mIsRefreshing) {
                    if (0 > mDetalY && -mHeaderHeight < mHeader.getPaddingTop()) { //未被隐藏上滑
                        mHeader.setPadding(0, (int) mDetalY, 0, 0);
                        if (-mDetalY > mHeaderHeight - 10) {
                            mIsRefreshing = false;
                            mHeader.setPadding(0, -mHeaderHeight, 0, 0);
                            return true;
                        }
                    } else
                        mHeader.setPadding(0, (int) mDetalY, 0, 0);
                } else {
                    int tempY = (int) (mDetalY - mHeaderHeight);
                    circleView.setmRefreshStart((int) (tempY * DEFAULT_VALUE));
                    mHeader.setPadding(0, tempY, 0, 0);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mChildView != null) {
                    if (-mHeaderHeight / 2 < mHeader.getPaddingTop()) {
                        circleView.start();
                        mIsRefreshing = true;
                        mHeader.setPadding(0, 0, 0, 0);
                        if (null != listener && !isFlag) {
                            isFlag = true;
                            listener.OnRefreshing();
                        }
                    } else {
                        mHeader.setPadding(0, -mHeaderHeight, 0, 0);
                        mIsRefreshing = false;
                    }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(mChildView, -1);
    }


    /**
     * 测量子孩子
     *
     * @param child
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
                params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void stopRefresh() {
        mIsRefreshing = false;
        isFlag = false;
        circleView.stop();
        mHeader.setPadding(0, -mHeaderHeight, 0, 0);
    }
}
