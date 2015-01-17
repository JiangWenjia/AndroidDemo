package com.example.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jiangwj on 2015/1/17.
 */
public class MProgressBar extends View {

    private int mFirstColor;

    private int mSecondColor;

    private int mSpeed;

    private int mProgress;

    private int mCircleWidth;

    private Paint mPaint;

    private Boolean isNext=false;

    public MProgressBar(Context context) {
        this(context, null);
    }

    public MProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a= context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar,defStyle,0);
        for(int i=0;i<a.length();i++){
            int attr=a.getIndex(i);
            switch (attr){
                case R.styleable.CustomProgressBar_firstColor:{
                    mFirstColor=a.getColor(attr, Color.BLUE);
                    break;
                }
                case R.styleable.CustomProgressBar_secondColor:{
                    mSecondColor=a.getColor(attr,Color.RED);
                    break;
                }
                case R.styleable.CustomProgressBar_speed:{
                    mSpeed=a.getInt(attr,30);
                    break;
                }
                case  R.styleable.CustomProgressBar_circleWidth:{
                    mCircleWidth= a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics()));
                    break;
                }
            }
        }
        a.recycle();
        mPaint=new Paint();


        new Thread() {
            public void run() {
                while (true) {
                    mProgress++;
                    if (mProgress == 360) {
                        mProgress = 0;
                        if (!isNext)
                            isNext = true;
                        else
                            isNext = false;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(mSpeed);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }.start();



    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center=getWidth()/2;

        int radius=center-mCircleWidth/2;


        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        RectF oval=new RectF(center-radius,center-radius,center+radius,center+radius);

        if(!isNext){
            mPaint.setColor(mFirstColor);
            canvas.drawCircle(center,center,radius,mPaint);
            mPaint.setColor(mSecondColor);
            canvas.drawArc(oval,-90,mProgress,false,mPaint);
        }else {
            mPaint.setColor(mSecondColor);
            canvas.drawCircle(center,center,radius,mPaint);
            mPaint.setColor(mFirstColor);
            canvas.drawArc(oval,-90,mProgress,false,mPaint);
        }
    }
}
