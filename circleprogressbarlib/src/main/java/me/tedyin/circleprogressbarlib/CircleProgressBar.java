package me.tedyin.circleprogressbarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 圆形进度条
 * Created by ted on 3/24/15.
 */
public class CircleProgressBar extends View {

    private static final float MAX_PROGRESS = 100f;

    private Context mContext;

    private int mCurrentProgress = 0;
    private int mLastProgress = mCurrentProgress;

    private int mCpbProgressColor = Color.parseColor("#0dfb7d");
    private int mCpbProgressBackgroundColor = Color.parseColor("#4aa6f5");
    private int mCpbProgressTextColor = mCpbProgressColor;
    private int mCpbBackgroundColor = Color.parseColor("#7df5f5f5");
    private int mStrokeWidth = 16;
    private int mMinWidth;

    private AtomicBoolean isStartLoading = new AtomicBoolean(false);

    //画圆所在的距形区域
    private RectF mRectF, mBgRectF;
    private Shader mShader;// todo test

    private Paint mCpbBgPaint;
    private Paint mCpbPaint;
    private Paint mCpbTextPaint;

    public CircleProgressBar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        parseAttributes(attrs);
        init();
    }

    private void parseAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        mCpbProgressColor = a.getColor(R.styleable.CircleProgressBar_cpbProgressColor, mCpbProgressColor);
        mCpbProgressBackgroundColor = a.getColor(R.styleable.CircleProgressBar_cpbBackgroundProgressColor, mCpbProgressBackgroundColor);
        mCpbBackgroundColor = a.getColor(R.styleable.CircleProgressBar_cpbBackgroundColor, mCpbBackgroundColor);
        mCpbProgressTextColor = a.getColor(R.styleable.CircleProgressBar_cpbProgressTextColor, mCpbProgressTextColor);
        mStrokeWidth = a.getInt(R.styleable.CircleProgressBar_cpbStrokeWidth, mStrokeWidth);
        a.recycle();
    }

    private void init() {
        // init background paint
        mCpbBgPaint = new Paint();
        mCpbBgPaint.setAntiAlias(true);
        mCpbBgPaint.setStyle(Paint.Style.STROKE);// todo
        mCpbBgPaint.setStrokeWidth(mStrokeWidth);
        mCpbBgPaint.setColor(mCpbBackgroundColor);

        // init fill paint
        mCpbPaint = new Paint();
        mCpbPaint.setAntiAlias(true);
        mCpbPaint.setDither(true);
        mCpbPaint.setStyle(Paint.Style.STROKE);// todo
        mCpbPaint.setStrokeCap(Paint.Cap.ROUND);
        mCpbPaint.setStrokeWidth(mStrokeWidth);
        mCpbPaint.setColor(mCpbProgressColor);

        // init text paint
        mCpbTextPaint = new Paint();
        mCpbTextPaint.setAntiAlias(true);
        mCpbTextPaint.setColor(mCpbProgressTextColor);

        // init rect
        mBgRectF = new RectF();
        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mMinWidth = Math.min(getWidth(), getHeight());
            int rLeft = mStrokeWidth / 2;
            int rTop = mStrokeWidth / 2;
            int rRight = mMinWidth - mStrokeWidth / 2;
            int rBottom = mMinWidth - mStrokeWidth / 2;
            mBgRectF.set(rLeft, rTop, rRight, rBottom);
            mRectF.set(rLeft, rTop, rRight, rBottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mBgRectF, 0, 360, true, mCpbBgPaint);
        drawCircleProgress(canvas);
        drawProgressText(canvas);
    }

    private void drawProgressText(Canvas canvas) {
        String text = String.valueOf(mCurrentProgress);
        int textSize = (int) ((mMinWidth - mStrokeWidth * 2) / 2.5);
        mCpbTextPaint.setTextSize(textSize);
        if (mShader != null) mCpbTextPaint.setShader(mShader);
        Paint.FontMetrics fm = mCpbTextPaint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        float textWidth = mCpbTextPaint.measureText(text);
        float x = (mMinWidth - textWidth) / 2;
        //文字绘制时，绘制位置应设置为文字底部的位置
        float y = (mMinWidth - textHeight) / 2 + textSize;
        canvas.drawText(text, x, y, mCpbTextPaint);
    }

    private void drawCircleProgress(Canvas canvas) {
        int startAngle = (int) ((mLastProgress / MAX_PROGRESS) * 360 - 90);//从12点方向开始画
        int sweepAngle = (int) ((mCurrentProgress / MAX_PROGRESS) * 360);//从12点方向开始画
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mCpbPaint);
        mLastProgress = mCurrentProgress;
    }

    /**
     * 刷新View
     */
    public void invalidateUi() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setProgress(int progress) {
        this.mCurrentProgress = progress;
        invalidateUi();
    }

    // 测试Shader的使用情况
    void shaderTest() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame);
        Shader mShader0 = new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        // 线性渐变
        Shader mShader1 = new LinearGradient(0, 0, 100, 100, new int[]{
                Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED}, null,
                Shader.TileMode.MIRROR);
        // 环形渐变
        Shader mShader2 = new RadialGradient(0, 0, 200, Color.YELLOW, Color.RED, Shader.TileMode.CLAMP);
        // 组合渐变
        mShader = new ComposeShader(mShader0, mShader1, PorterDuff.Mode.LIGHTEN);
    }
}

