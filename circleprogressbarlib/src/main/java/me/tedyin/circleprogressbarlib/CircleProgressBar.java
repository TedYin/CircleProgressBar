package me.tedyin.circleprogressbarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形进度条
 * Created by ted on 3/24/15.
 */
public class CircleProgressBar extends View {

    private static final String KEY_INSTANCE_STATE = "instance_state";
    private static final String KEY_STATE_CURRENT_PROGRESS = "state_current_progress";
    private static final String KEY_STATE_ANGLE_STEP = "state_angle_step";
    private static final String KEY_STATE_NEED_SHOW_TEXT = "state_need_show_text";
    private static final String KEY_STATE_NEED_ANIM = "state_need_anim";

    private static final float MAX_PROGRESS = 100f;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    private int mCpbProgressColor = Color.parseColor("#0dfb7d");
    private int mCpbProgressTextColor;
    private int mCpbBackgroundColor = Color.parseColor("#7df5f5f5");
    private int mCpbStrokeWidth = 10;// default stroke width
    private int mCpbStartAngle = -90;// default 12 o'clock
    private int mCpbMaxAngle = 360;// default complete circle
    private boolean mCpbNeedAnim = true;// default start anim
    private boolean mCpbNeedShowText = true;// default show text

    private RectF mRectF, mBgRectF;
    private Paint mCpbBgPaint;
    private Paint mCpbPaint;
    private Paint mCpbTextPaint;
    private int mAngleStep = 0;
    private int mMinWidth;
    private int mCurrentProgress = 0;
    private int[] mColorScheme;
    private AnimRunnable mAnimRunnable;
    private LoadingCallBack mLoadingCallBack;

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
        mCpbBackgroundColor = a.getColor(R.styleable.CircleProgressBar_cpbBackgroundColor, mCpbBackgroundColor);
        mCpbProgressTextColor = a.getColor(R.styleable.CircleProgressBar_cpbProgressTextColor, mCpbProgressTextColor);
        mCpbStrokeWidth = a.getInt(R.styleable.CircleProgressBar_cpbStrokeWidth, mCpbStrokeWidth);
        mCpbStartAngle = a.getInt(R.styleable.CircleProgressBar_cpbStartAngle, mCpbStartAngle);
        mCpbMaxAngle = a.getInt(R.styleable.CircleProgressBar_cpbMaxAngle, mCpbMaxAngle);
        mCpbNeedShowText = a.getBoolean(R.styleable.CircleProgressBar_cpbNeedShowText, mCpbNeedShowText);
        mCpbNeedAnim = a.getBoolean(R.styleable.CircleProgressBar_cpbNeedAnim, mCpbNeedAnim);

        mCpbNeedAnim = mCpbMaxAngle % 360 == 0 && mCpbNeedAnim;
        mCpbProgressTextColor = mCpbProgressTextColor == 0 ? mCpbProgressColor : mCpbProgressTextColor;
        a.recycle();
    }

    private void init() {
        initBackgroundPaint();
        initFillPaint();
        initTextPaint();
        initRectF();
        initAnim();
    }

    // init background paint
    private void initBackgroundPaint() {
        mCpbBgPaint = new Paint();
        mCpbBgPaint.setAntiAlias(true);
        mCpbBgPaint.setStyle(Paint.Style.STROKE);
        mCpbBgPaint.setStrokeWidth(mCpbStrokeWidth);
        mCpbBgPaint.setColor(mCpbBackgroundColor);
    }

    // init fill paint
    private void initFillPaint() {
        mCpbPaint = new Paint();
        mCpbPaint.setAntiAlias(true);
        mCpbPaint.setDither(true);
        mCpbPaint.setStyle(Paint.Style.STROKE);
        mCpbPaint.setStrokeCap(Paint.Cap.ROUND);
        mCpbPaint.setStrokeWidth(mCpbStrokeWidth);
        mCpbPaint.setColor(mCpbProgressColor);
    }

    // init text paint
    private void initTextPaint() {
        mCpbTextPaint = new Paint();
        mCpbTextPaint.setAntiAlias(true);
        mCpbTextPaint.setColor(mCpbProgressTextColor);
    }

    // init rectF
    private void initRectF() {
        mBgRectF = new RectF();
        mRectF = new RectF();
    }

    // init Animation Runnable
    private void initAnim() {
        if (mCpbNeedAnim) {
            mAnimRunnable = new AnimRunnable();
        }
    }

    // init shader
    private void initShader() {
        if (mColorScheme == null || mColorScheme.length == 0) return;
        float end = mMinWidth - mCpbStrokeWidth / 2;
        Shader shader = new LinearGradient(0, 0, end, end, mColorScheme, null,
                Shader.TileMode.CLAMP);
        mCpbPaint.setShader(shader);
        if (mCpbProgressTextColor != mCpbProgressColor || !mCpbNeedShowText) return;
        mCpbTextPaint.setShader(shader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        startAnimIfNeed();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mMinWidth = Math.min(getWidth(), getHeight());
            int rLeft = mCpbStrokeWidth / 2;
            int rTop = mCpbStrokeWidth / 2;
            int rRight = mMinWidth - mCpbStrokeWidth / 2;
            int rBottom = mMinWidth - mCpbStrokeWidth / 2;
            mBgRectF.set(rLeft, rTop, rRight, rBottom);
            mRectF.set(rLeft, rTop, rRight, rBottom);
            initShader();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleBgProgress(canvas);
        drawCircleProgress(canvas);
        drawProgressText(canvas);
    }

    private void drawCircleBgProgress(Canvas canvas) {
        canvas.drawArc(mBgRectF, mCpbStartAngle, mCpbMaxAngle, false, mCpbBgPaint);
    }

    private void drawProgressText(Canvas canvas) {
        if (!mCpbNeedShowText) return;
        String text = String.valueOf(mCurrentProgress);
        int textSize = (int) ((mMinWidth - mCpbStrokeWidth * 2) / 2.5);
        mCpbTextPaint.setTextSize(textSize);
        Paint.FontMetrics fm = mCpbTextPaint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        float textWidth = mCpbTextPaint.measureText(text);
        float x = (mMinWidth - textWidth) / 2;
        float y = (mMinWidth - textHeight) / 2 + textSize;
        canvas.drawText(text, x, y, mCpbTextPaint);
    }

    private void drawCircleProgress(Canvas canvas) {
        int startAngle = mAngleStep + mCpbStartAngle;
        int sweepAngle = (int) ((mCurrentProgress / MAX_PROGRESS) * mCpbMaxAngle);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mCpbPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimRunnable != null) {
            mHandler.removeCallbacks(mAnimRunnable);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        state.putInt(KEY_STATE_CURRENT_PROGRESS, mCurrentProgress);
        state.putInt(KEY_STATE_ANGLE_STEP, mAngleStep);
        state.putBoolean(KEY_STATE_NEED_SHOW_TEXT, mCpbNeedShowText);
        state.putBoolean(KEY_STATE_NEED_ANIM, mCpbNeedAnim);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentProgress = bundle.getInt(KEY_STATE_CURRENT_PROGRESS);
            mAngleStep = bundle.getInt(KEY_STATE_ANGLE_STEP);
            mCpbNeedAnim = bundle.getBoolean(KEY_STATE_NEED_ANIM);
            mCpbNeedShowText = bundle.getBoolean(KEY_STATE_NEED_SHOW_TEXT);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void startAnimIfNeed() {
        if (mAnimRunnable != null) {
            mHandler.removeCallbacks(mAnimRunnable);
        }

        if (mCpbNeedAnim) {
            mHandler.post(mAnimRunnable);
        }
    }

    private class AnimRunnable implements Runnable {
        @Override
        public void run() {
            if (mCurrentProgress >= MAX_PROGRESS) {
                mCurrentProgress = (int) MAX_PROGRESS;
                invalidateView();
                mHandler.removeCallbacks(this);
                if (mLoadingCallBack != null) {
                    mLoadingCallBack.loadingComplete(CircleProgressBar.this);
                }
            } else {
                invalidateView();
                mHandler.postDelayed(this, 12);
            }
        }

        private void invalidateView() {
            mAngleStep += 2;
            invalidate();
        }

    }

    /**
     * invalidate view
     */
    public void invalidateUi() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setProgress(int progress) {
        this.mCurrentProgress = progress > MAX_PROGRESS ? (int) MAX_PROGRESS : progress;
        if (!mCpbNeedAnim) {
            invalidateUi();
        }
    }

    /**
     * set color scheme
     *
     * @param colorScheme colors
     */
    public void setColorScheme(int... colorScheme) {
        mColorScheme = colorScheme;
    }

    /**
     * loading complete callback
     *
     * @param callBack callback
     */
    public void setLoadingCallBack(LoadingCallBack callBack) {
        this.mLoadingCallBack = callBack;
    }

    public interface LoadingCallBack {
        public void loadingComplete(View v);
    }
}

