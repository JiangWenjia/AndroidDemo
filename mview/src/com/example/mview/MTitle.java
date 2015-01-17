package com.example.mview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jiangwj on 2015/1/17.
 */
public class MTitle extends View {

    private String mTitleText;

    private int mTitleColor;

    private int mTitleSize;

    private Paint paint;

    private Rect bound;



    public MTitle(Context context) {
        this(context, null);
    }

    public MTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.mTitle,defStyle,0);
        for(int i=0;i<a.getIndexCount();i++){
            int attr=a.getIndex(i);
            switch (a.getIndex(i)){
                case R.styleable.mTitle_titleText:{
                    mTitleText=a.getString(attr);
                    break;
                }
                case R.styleable.mTitle_titleColor:{
                    mTitleColor=a.getColor(attr, Color.BLACK);
                    break;
                }
                case R.styleable.mTitle_titleSize:{
                    //默认16sp
                    mTitleSize=a.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics()));
                    break;
                }
            }
        }
        a.recycle();
        paint=new Paint();
        paint.setTextSize(mTitleSize);

        bound=new Rect();
        paint.getTextBounds(mTitleText,0,mTitleText.length(),bound);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if(widthMode==MeasureSpec.EXACTLY){
            width=widthSize;
        }else{
            paint.setTextSize(mTitleSize);
            paint.getTextBounds(mTitleText, 0, mTitleText.length(), bound);
            float textWidth = bound.width();
            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }

        if(heightMode==MeasureSpec.EXACTLY){
            height=heightSize;
        }else{
            paint.setTextSize(mTitleSize);
            paint.getTextBounds(mTitleText,0,mTitleText.length(),bound);
            float textHeight=bound.height();
            int desired=(int)(getPaddingBottom()+textHeight+getPaddingTop());
            height=desired;
        }
        /**
         * 这里传入的measureSpec是一直从measure()方法中传递过来的。
         * 然后调用MeasureSpec.getMode()方法可以解析出specMode，
         * 调用MeasureSpec.getSize()方法可以解析出specSize。
         * 接下来进行判断，如果specMode等于AT_MOST或EXACTLY就返回specSize，
         * 这也是系统默认的行为。之后会在onMeasure()方法中调用setMeasuredDimension()方法来设定测量出的大小，这样一次measure过程就结束了。
         * 在setMeasuredDimension()方法调用之后，我们才能使用getMeasuredWidth()和getMeasuredHeight()来获取视图测量出的宽高，
         * 以此之前调用这两个方法得到的值都会是0
         */
        setMeasuredDimension(width,height);
    }



    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(Color.GREEN);
        /**
         * 首先getMeasureWidth()方法在measure()过程结束后就可以获取到了
         * ，而getWidth()方法要在layout()过程结束后才能获取到。
         * 另外，getMeasureWidth()方法中的值是通过setMeasuredDimension()方法来进行设置的，
         * 而getWidth()方法中的值则是通过视图右边的坐标减去左边的坐标计算出来的。

         观察SimpleLayout中onLayout()方法的代码，
         这里给子视图的layout()方法传入的四个参数分别是0、0、childView.getMeasuredWidth()和childView.getMeasuredHeight()，
         因此getWidth()方法得到的值就是childView.getMeasuredWidth()- 0 = childView.getMeasuredWidth() ，
         所以此时getWidth()方法和getMeasuredWidth()得到的值就是相同的，但如果你将onLayout()方法中的代码进行如下修改：
         */
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
       // 父布局修改了onLayout方法之后才会不同
        paint.setColor(mTitleColor);

        canvas.drawText(mTitleText, getWidth() / 2 - bound.width() / 2, getHeight() / 2 + bound.height()/2, paint);
    }
}
// MeasureSpec相关知识点：
/**
  * MeasureSpec的值由specSize和specMode共同组成的，其中specSize记录的是大小，specMode记录的是规格。specMode一共有三种类型，如下所示：

 1.EXACTLY

 表示父视图希望子视图的大小应该是由specSize的值来决定的，系统默认会按照这个规则来设置子视图的大小，开发人员当然也可以按照自己的意愿设置成任意的大小。

 2.AT_MOST

 表示子视图最多只能是specSize中指定的大小，开发人员应该尽可能小得去设置这个视图，并且保证不会超过specSize。系统默认会按照这个规则来设置子视图的大小，开发人员当然也可以按照自己的意愿设置成任意的大小。

 3.UNSPECIFIED

 表示开发人员可以将视图按照自己的意愿设置成任意的大小，没有任何限制。这种情况比较少见，不太会用到。

 那么你可能会有疑问了，widthMeasureSpec和heightMeasureSpec这两个值又是从哪里得到的呢？通常情况下，这两个值都是由父视图经过计算后传递给子视图的，说明父视图会在一定程度上决定子视图的大小。但是最外层的根视图，它的widthMeasureSpec和heightMeasureSpec又是从哪里得到的呢？这就需要去分析ViewRoot中的源码了，观察performTraversals()方法可以发现如下代码：


 1.  childWidthMeasureSpec = getRootMeasureSpec(desiredWindowWidth, lp.width);

 2. childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);

 可以看到，这里调用了getRootMeasureSpec()方法去获取widthMeasureSpec和heightMeasureSpec的值，注意方法中传入的参数，其中lp.width和lp.height在创建ViewGroup实例的时候就被赋值了，它们都等于MATCH_PARENT。然后看下getRootMeasureSpec()方法中的代码，如下所示：


 1.  private int getRootMeasureSpec(int windowSize, int rootDimension) {

 2.     int measureSpec;

 3.     switch (rootDimension) {

 4.     case ViewGroup.LayoutParams.MATCH_PARENT:

 5.         measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.EXACTLY);

 6.         break;

 7.     case ViewGroup.LayoutParams.WRAP_CONTENT:

 8.         measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.AT_MOST);

 9.         break;

 10.     default:

 11.         measureSpec = MeasureSpec.makeMeasureSpec(rootDimension, MeasureSpec.EXACTLY);

 12.         break;

 13.     }

 14.     return measureSpec;

 15. }

 可以看到，这里使用了MeasureSpec.makeMeasureSpec()方法来组装一个MeasureSpec，当rootDimension参数等于MATCH_PARENT的时候，MeasureSpec的specMode就等于EXACTLY，当rootDimension等于WRAP_CONTENT的时候，MeasureSpec的specMode就等于AT_MOST。并且MATCH_PARENT和WRAP_CONTENT时的specSize都是等于windowSize的，也就意味着根视图总是会充满全屏的。
 *
 */
