package com.zwb.touchpullview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by zwb
 * Description
 * Date 2017/6/13.
 */

public class TouchPullView extends View {
    private int mRadius = 40;//圆的半径
    private int mMaxHeight = 300;//最大高度
    private float mRatio;//下拉系数 0-1
    private Paint mCirclePaint;
    private float mCenterX, mCenterY;
    private Paint mPathPaint;
    private Path mPath;

    private int mTargetAngle = 115;//终点移动的最大角度
    private float mEndPointX, mEndPointY;//终点的坐标
    private float mStartPointX, mStartPointY;//起点的坐标
    private float mControlPointX, mControlPointY;//起点的坐标
    private Paint mPointPaint;
    private int mMinWidth = 400;//最小宽度，就是起点能到达的极限位置

    private DecelerateInterpolator ratioInterpolator = new DecelerateInterpolator();
    private Interpolator mAngleInterpolator;//角度插值器

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

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);
        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setColor(0x66ff00ff);
        mPath = new Path();

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setDither(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.BLUE);

        //拉到2倍的半径高度是角度为90度
        mAngleInterpolator = PathInterpolatorCompat.create(
                (mRadius * 2.0f) / mMaxHeight, 90.0f / mTargetAngle
        );
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
        mCenterY = getMeasuredHeight() - mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStartPoint(canvas);
        drawEndPoint(canvas);
        drawControlPoint(canvas);

        mPath.reset();
        mPath.moveTo(getMeasuredWidth(), 0);
        mPath.lineTo(mStartPointX, mStartPointY);
        mPath.quadTo(mControlPointX, mControlPointY, mEndPointX, mEndPointY);
        mPath.lineTo(2 * mCenterX - mEndPointX, mEndPointY);
        mPath.quadTo(2 * mCenterX - mControlPointX, mControlPointY, getMeasuredWidth() - mStartPointX, 0);
        mPath.close();
        canvas.drawPath(mPath, mPathPaint);

        mCirclePaint.setColor(Color.RED);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mCirclePaint);
        mCirclePaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, mRadius - 10, mCirclePaint);
        canvas.drawCircle(mCenterX, mCenterY, 5, mPointPaint);
    }

    /**
     * 绘制起点
     *
     * @param canvas
     */
    private void drawStartPoint(Canvas canvas) {
        mStartPointX = (getMeasuredWidth() - mMinWidth) / 2 * mRatio;
        mStartPointY = 0;
        canvas.drawCircle(mStartPointX, mStartPointY, 5, mPointPaint);
    }

    /**
     * 绘制终点
     *
     * @param canvas
     */
    private void drawEndPoint(Canvas canvas) {
        float angle = mTargetAngle * mAngleInterpolator.getInterpolation(mRatio);
        double dx = mRadius * Math.sin(Math.PI / 180 * angle);
        double dy = mRadius * Math.cos(Math.PI / 180 * angle);
        mEndPointX = (float) (mCenterX - dx);
        mEndPointY = (float) (mCenterY + dy);
        canvas.drawCircle(mEndPointX, mEndPointY, 5, mPointPaint);
//        canvas.drawCircle(getMeasuredWidth() - mEndPointX, mEndPointY, 5, mPointPaint);
    }

    /**
     * 绘制控制点
     *
     * @param canvas
     */
    private void drawControlPoint(Canvas canvas) {
        float angle = mTargetAngle * mAngleInterpolator.getInterpolation(mRatio);
        float dx = (float) ((mEndPointY - 10) / Math.tan(Math.PI / 180 * angle));
        mControlPointX = mEndPointX - dx;
        mControlPointY = 10;//控制点稍微往下偏移10个像素
        canvas.drawCircle(mControlPointX, mControlPointY, 5, mPointPaint);
//        canvas.drawCircle(getMeasuredWidth() - mControlPointX, mControlPointY, 5, mPointPaint);
    }

    public void setmRatio(float ratio) {
//        mRatio = ratio;
        mRatio = ratioInterpolator.getInterpolation(ratio);
        requestLayout();
    }

    /**
     * 恢复初始状态
     */
    public void resetRatio() {
        ValueAnimator animator = ValueAnimator.ofFloat(mRatio, 0);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRatio = (float) animation.getAnimatedValue();
                requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRatio = 0;
                requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

}
