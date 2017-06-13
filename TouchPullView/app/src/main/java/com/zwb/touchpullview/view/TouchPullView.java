package com.zwb.touchpullview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zwb
 * Description
 * Date 2017/6/13.
 */

public class TouchPullView extends View {
    private int mRadius = 150;//圆的半径
    private int mMaxHeight = mRadius * 4;//最大高度
    private float mRatio;//下拉系数 0-1
    private Paint mCirclePaint;
    private float mCenterX, mCenterY;

    public TouchPullView(Context context) {
        this(context, null);
    }

    public TouchPullView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchPullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int minWidth = 2 * mRadius + getPaddingLeft() + getPaddingRight();
        int resultWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            resultWidth = widthSize;
        } else {
            resultWidth = Math.min(widthSize, minWidth);
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int maxHeight = (int) (mMaxHeight * mRatio + 0.5f) + getPaddingTop() + getPaddingBottom();
        int resultHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = heightSize;
        } else {
            resultHeight = Math.min(heightSize, maxHeight);
        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = getMeasuredWidth() / 2.0f;
        mCenterY = getMeasuredHeight() / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mCirclePaint);
    }

    public void setmRatio(float ratio) {
        mRatio = ratio;
        requestLayout();
    }

}
