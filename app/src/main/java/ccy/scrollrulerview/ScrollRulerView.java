package ccy.scrollrulerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by ccy on 2017-10-14.
 * 小广告：
 * 本人github：https://github.com/CCY0122
 * 我是2017届毕业生，目前就职于杭州雄迈信息，工作经验：2016.12——至今
 * 目前正积极寻找新的工作平台，有工作机会推荐可联系我~qq：671518768
 */

public class ScrollRulerView extends View {

    private static int DEFAULT_PRIMARY_COLOR = 0xFF3CB371;
    private static int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static int DEFAULT_LINE_COLOR = Color.GRAY;
    private static int DEFAULT_RULER_BACKGROUND = 0x00000000; //默认没颜色
    private static String DEFAULT_UNIT = "kg";
    private static int DEFAULT_START_NUM = 0;
    private static int DEFAULT_END_NUM = 100;


    private int primaryColor;  //主题颜色
    private float primaryTextSize;
    private int textColor;  //刻度字体颜色
    private float textSize;
    private int lineColor;


    private int rulerBackground;
    private String unit;  //单位字符串
    private int startNum; //起点值
    private int endNum; //终点值
    private float minGap; //最小刻度间隔（一个间隔相差值为0.1）

    private float minWidth; //View的最小宽度
    private float minHeight; //View的最小高度
    private float contentWidth; //内容的宽度（可以大致理解为实际刻度线的长度）
    private float lineLength; //刻度长度

    private Paint primaryPaint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint backgroundPaint;

    private Rect textRect = new Rect();
    private Rect primaryTextRect = new Rect();

    private Scroller scroller;
    private ViewConfiguration viewConfiguration;
    private VelocityTracker velocityTracker;

    public ScrollRulerView(Context context) {
        this(context, null);
    }

    public ScrollRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollRulerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        scroller = new Scroller(context);
        viewConfiguration = ViewConfiguration.get(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollRulerView);
        primaryColor = ta.getColor(R.styleable.ScrollRulerView_primary_color, DEFAULT_PRIMARY_COLOR);
        primaryTextSize = ta.getDimension(R.styleable.ScrollRulerView_primary_text_size, sp2px(30));
        textColor = ta.getColor(R.styleable.ScrollRulerView_text_color, DEFAULT_TEXT_COLOR);
        textSize = ta.getDimension(R.styleable.ScrollRulerView_text_size, sp2px(15));
        lineColor = ta.getColor(R.styleable.ScrollRulerView_line_color, DEFAULT_LINE_COLOR);
        rulerBackground = ta.getColor(R.styleable.ScrollRulerView_rulerBackgroundColor,DEFAULT_RULER_BACKGROUND);
        unit = ta.getString(R.styleable.ScrollRulerView_unit);
        unit = unit == null ? DEFAULT_UNIT : unit;
        startNum = ta.getInt(R.styleable.ScrollRulerView_start_num, DEFAULT_START_NUM);
        endNum = ta.getInt(R.styleable.ScrollRulerView_end_num, DEFAULT_END_NUM);
        minGap = ta.getDimension(R.styleable.ScrollRulerView_min_gap, dp2px(10));
        ta.recycle();

        initSize();

        initPaint();

    }


    private void initPaint() {
        primaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        primaryPaint.setColor(primaryColor);
        primaryPaint.setTextSize(primaryTextSize);
        primaryPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(dp2px(1.5f));  //刻度线粗
//        linePaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setColor(rulerBackground);

    }

    private void initSize() {
        lineLength = dp2px(15); //默认
        minWidth = dp2px(200); //默认
        minHeight = dp2px(120);  //默认
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode != MeasureSpec.EXACTLY) {
            wSize = (int) minWidth;
        }
        if (hMode != MeasureSpec.EXACTLY) {
            hSize = (int) minHeight;
        }
        setMeasuredDimension(wSize, hSize);

        //setMeasuredDimension结束后，即可计算出内容实际宽度
        //多加了一个MeasuredWidth是因为最左端和最右端将各空出半个MeasuredWidth
        contentWidth = Math.abs(endNum - startNum) * 10 * minGap + getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawScaleplate(canvas);  //画刻度板

        drawPrimaryText(canvas); //画当前刻度值

    }


    /**
     * 画刻度线板
     *
     * @param canvas
     */
    private void drawScaleplate(Canvas canvas) {
        canvas.save();

        float startX, startY, endX, endY; //起点和终点

        //1、画尺子背景色

        textPaint.getTextBounds("0", 0, "0".length(), textRect);  //简单的用"0"代表之后刻度值的高度
        startX = 0;
        endX = startX + contentWidth;
        startY = getMeasuredHeight() / 2;
        endY = startY + 2*lineLength + textRect.height() + dp2px(15);
        canvas.drawRect(startX
        ,startY
        ,endX
        ,endY
        ,backgroundPaint);

        //2、画一条长直线

        startY = endY = getMeasuredHeight() / 2;
        canvas.drawLine(startX
                , startY
                , endX
                , endY
                , linePaint);

        //3、画直线下的刻度和对应的数值

        startX = endX = getMeasuredWidth() / 2;   //从View的一半宽开始画，否则滑动的时候前半段刻度就滑不到了
        int lineCount = Math.abs(endNum - startNum) * 10;  //刻度的数量
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float textCenterY;
        for (int i = 0; i <= lineCount; i++) {
            if (i % 10 == 0) {   //整数，要画对应值

                endY = startY + 2 * lineLength;

                String value = (endNum > startNum) ? startNum + i / 10 + "" : startNum - i / 10 + ""; //刻度值（要考虑到endNum<startNum的情况)
                textPaint.getTextBounds(value, 0, value.length(), textRect);
                textCenterY = endY + dp2px(10) + textRect.height() / 2 - (metrics.ascent + metrics.descent) / 2;
                canvas.drawText(value
                        , startX
                        , textCenterY
                        , textPaint);
            } else {
                endY = startY + lineLength;
            }


            canvas.drawLine(startX
                    , startY
                    , endX
                    , endY
                    , linePaint);

            startX = endX = startX + minGap;  //每次往右移动一个minGap的长度

        }

        //4、最后把中间的的刻度指针画上

        startY = startY + linePaint.getStrokeWidth() / 2;  //细节调整！！向下偏移半个线段粗

        linePaint.setColor(primaryColor);
        linePaint.setStrokeWidth(linePaint.getStrokeWidth() * 2.5f); //2.5倍粗

        startX = endX = getMeasuredWidth() / 2 + getScrollX();
        endY = startY + 2 * lineLength + dp2px(2);
        canvas.drawLine(startX, startY, endX, endY, linePaint);

        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(linePaint.getStrokeWidth() / 2.5f);

        canvas.restore();
    }


    /**
     * 画当前刻度数值
     *
     * @param canvas
     */
    private void drawPrimaryText(Canvas canvas) {
        canvas.save();


        //1、画数值
        float centerX = getScrollX() + getMeasuredWidth() / 2;
        float centerY = 0.25f * getMeasuredHeight() - (primaryPaint.getFontMetrics().ascent + primaryPaint.getFontMetrics().descent) / 2;
        String currentValue = getCurrentValue() + "";
        primaryPaint.getTextBounds(currentValue, 0, currentValue.length(), primaryTextRect);

        canvas.drawText(currentValue
                , centerX
                , centerY
                , primaryPaint);


        //2、画单位
        primaryPaint.setTextSize(primaryTextSize * 0.35f);
        centerX = centerX + primaryTextRect.width() / 2 + primaryPaint.measureText(unit) / 2 + dp2px(5); //位于数值右侧多5dp处
        centerY = centerY - primaryTextRect.height() * 0.75f;  //  位于数值0.25高度处
        canvas.drawText(unit
                , centerX
                , centerY
                , primaryPaint);

        primaryPaint.setTextSize(primaryTextSize);

        canvas.restore();
    }

    private float getCurrentValue() {
        float value; //当前值的10倍（为了取得一位小数点）
        int gapCount = (int) (getScrollX() / minGap);  //已经滑过的间隔数量
        if(startNum < endNum){  //刻度从小到大
            value = startNum * 10  + gapCount;
        }else {     //刻度从大到小
            value = startNum * 10  - gapCount;
        }
        return value / 10.0f;
    }

    /**
     * 设置当前值
     * @param currentValue
     */
    public void setCurrentValue(float currentValue){
        float value = currentValue; //取整
        int gapCount;

        //当前值要在刻度范围之内
        if(startNum < endNum){    //刻度是从小到大的
            value = Math.max(startNum,value);
            value = Math.min(endNum,value);
            gapCount = (int) ((value - startNum) * 10);
        }else {     //刻度是从大到小的
            value = Math.max(endNum,value);
            value = Math.min(startNum,value);
            gapCount = (int) ((startNum - value) * 10);
        }

        final float scrollx = gapCount * minGap;
        post(new Runnable() {
            @Override
            public void run() {
                scrollTo((int) scrollx,0);
            }
        });

    }


    private float lastX = 0;
    private float x = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {  //fling还没结束
                    scroller.abortAnimation();
                }
                lastX = x = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                int deltaX = (int) (lastX - x);
                if (getScrollX() + deltaX < 0) {    //越界恢复
                    scrollTo(0, 0);
                    return true;
                } else if (getScrollX() + deltaX > contentWidth - getMeasuredWidth()) {
                    scrollTo((int) (contentWidth - getMeasuredWidth()), 0);
                    return true;
                }
                scrollBy(deltaX, 0);
                lastX = x;
                break;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                velocityTracker.computeCurrentVelocity(1000);  //计算1秒内滑动过多少像素
                int xVelocity = (int) velocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > viewConfiguration.getScaledMinimumFlingVelocity()) {  //滑动速度可被判定为抛动
                    scroller.fling(getScrollX(), 0, -xVelocity, 0, 0, (int) (contentWidth - getMeasuredWidth()), 0, 0);
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }


    //以下为各属性setter/getter
    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        primaryPaint.setColor(primaryColor);
        invalidate();
    }

    public float getPrimaryTextSize() {
        return primaryTextSize;
    }

    public void setPrimaryTextSize(float primaryTextSize) {
        this.primaryTextSize = primaryTextSize;
        primaryPaint.setTextSize(primaryTextSize);
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        linePaint.setColor(lineColor);
        invalidate();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
        invalidate();
    }

    public int getEndNum() {
        return endNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
        invalidate();
    }

    public float getMinGap() {
        return minGap;
    }

    public void setMinGap(float minGap) {
        this.minGap = minGap;
        invalidate();
    }

    public int getRulerBackground() {
        return rulerBackground;
    }

    public void setRulerBackground(int rulerBackground) {
        this.rulerBackground = rulerBackground;
        backgroundPaint.setColor(rulerBackground);
        invalidate();
    }




    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

}
