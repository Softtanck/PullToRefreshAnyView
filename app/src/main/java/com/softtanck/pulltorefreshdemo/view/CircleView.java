package com.softtanck.pulltorefreshdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * @author : Tanck
 * @Description : TODO
 * @date 7/22/2015
 */
public class CircleView extends View {
    private int mWidth;
    private int mHeight;
    private boolean mIsStart;
    private int mRefreshStart = 330;
    private int mRefreshStop;
    private Paint mOutPaint;
    private RectF rectF;
    private ValueAnimator valueAnimator;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    /**
     * 设置开始角度
     *
     * @param mRefreshStart
     */
    public void setmRefreshStart(int mRefreshStart) {
        stop();
        this.mRefreshStart = mRefreshStart;
        invalidate();
    }

    @SuppressLint("NewApi")
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        mOutPaint = new Paint();
        mOutPaint.setStyle(Paint.Style.STROKE);
        mOutPaint.setColor(Color.BLUE);
        mOutPaint.setAntiAlias(true);
        mOutPaint.setStrokeWidth(8);

        valueAnimator = ValueAnimator.ofFloat(0, 60).setDuration(12000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });

        valueAnimator.setRepeatCount(-1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        rectF = new RectF(10, 10, mWidth - 10, mHeight - 10);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRefreshStart += mIsStart ? 3 : 10;
        mRefreshStop += mIsStart ? 10 : 3;
        mRefreshStart = mRefreshStart % 360;
        mRefreshStop = mRefreshStop % 360;

        int swipe = mRefreshStop - mRefreshStart;
        swipe = swipe < 0 ? swipe + 360 : swipe;

        canvas.drawArc(rectF,
                mRefreshStart, swipe, false, mOutPaint);
        if (swipe >= 330) {
            mIsStart = false;
        } else if (swipe <= 10) {
            mIsStart = true;
        }
    }

    public void start() {
        if (valueAnimator.isRunning())
            return;
        valueAnimator.start();
    }

    public void stop(){
        if(valueAnimator.isRunning())
            valueAnimator.cancel();
    }
}
