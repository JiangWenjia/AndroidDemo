package com.example.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by jiangwj on 2015/1/17.
 */
public class MImage extends View {

    private String mTitleText;

    private int mTitleColor;

    private int mTitleSize;

    private int scaleType;

    private Bitmap image;

    private Paint paint;

    private Rect bound;

    private Rect rect;

    private  int mHeight;

    private int mSize;



    public MImage(Context context) {
        this(context, null);
    }

    public MImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.mImage,defStyle,0);
        for(int i=0;i<a.getIndexCount();i++){
            int attr=a.getIndex(i);
            switch (a.getIndex(i)){
                case R.styleable.mImage_titleText:{
                    mTitleText=a.getString(attr);
                    break;
                }
                case R.styleable.mImage_titleColor:{
                    mTitleColor=a.getColor(attr, Color.BLACK);
                    break;
                }
                case R.styleable.mImage_titleSize:{
                    //默认16sp
                    mTitleSize=a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                }
                case R.styleable.mImage_imageScaleType:{
                    scaleType=a.getInt(attr,0);
                    break;
                }
                case R.styleable.mImage_image:{
                    image= BitmapFactory.decodeResource(getResources(),a.getResourceId(attr,0));
                    break;
                }
            }
        }
        a.recycle();
        paint=new Paint();
        bound=new Rect();
        rect = new Rect();
        paint.setTextSize(mTitleSize);
        // 计算了描绘字体需要的范围
        paint.getTextBounds(mTitleText, 0, mTitleText.length(), bound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
          int wMode=MeasureSpec.getMode(widthMeasureSpec);
          int wSize=MeasureSpec.getSize(widthMeasureSpec);

          int hMode=MeasureSpec.getMode(heightMeasureSpec);
          int hSize=MeasureSpec.getSize(heightMeasureSpec);


          if(wMode==MeasureSpec.EXACTLY){
              mSize=wSize;
          }else {
              int imageSize=image.getWidth()+getPaddingLeft()+getPaddingRight();
              int textSize=bound.width()+getPaddingLeft()+getPaddingRight();
              mSize=Math.max(imageSize,textSize);
              mSize=Math.min(mSize,wSize);
          }

          if(hMode==MeasureSpec.EXACTLY){
              mHeight=hSize;
          }else {
              mHeight=image.getHeight()+bound.height()+getPaddingTop()+getPaddingBottom();
              mHeight=Math.min(mHeight,hSize);
          }
        setMeasuredDimension(mSize,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        canvas.drawRect(0,0,getMeasuredWidth(),getHeight(),paint);

        rect.left = getPaddingLeft();
        rect.right = mSize - getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = mHeight - getPaddingBottom();


        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        if(bound.width()>mSize){
            TextPaint tPaint= new TextPaint(paint);
            String msg= TextUtils.ellipsize(mTitleText,tPaint,(float)mSize-getPaddingLeft()-getPaddingRight(),
                    TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg,getPaddingLeft(),mHeight-getPaddingBottom(),paint);
        }else{
            canvas.drawText(mTitleText,mSize/2-bound.width()*1.0f/2,mHeight-getPaddingBottom(),paint);
        }


        rect.bottom -= bound.height();
             if (scaleType == 0) {
                    canvas.drawBitmap(image, null, rect, paint);
             } else {
                      //计算居中的矩形范围
                    rect.left = mSize / 2 - image.getWidth() / 2;
                    rect.right = mSize / 2 + image.getWidth() / 2;
                    rect.top = (mHeight - bound.height()) / 2 - image.getHeight() / 2;
                    rect.bottom = (mHeight - bound.height()) / 2 + image.getHeight() / 2;

                    canvas.drawBitmap(image, null, rect, paint);
                }


    }
}
