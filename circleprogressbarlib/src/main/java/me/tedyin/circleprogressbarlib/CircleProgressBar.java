/*
 * Copyright (c) 2015. TedYin
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private int mCpbWholeBackgroundColor = Color.parseColor("#ffeeeaff");
    private int mCpbBackgroundColor = Color.parseColor("#7df5f5f5");
    private int mCpbForegroundColor = Color.parseColor("#0dfb7d");
    private int mCpbProgressTextColor;
    private int mCpbStrokeWidth = 10;// default stroke width
    private int mCpbStartAngle = -90;// default 12 o'clock
    private int mCpbMaxAngle = 360;// default complete circle
    private boolean mCpbNeedAnim = true;// default start anim
    private boolean mCpbNeedShowText = true;// default show text

    private RectF mWholeRectF, mForegroundRectF, mBackgroundRectF;
    private Paint mCpbWholeBackgroundPaint;
    private Paint mCpbBackgroundPaint;
    private Paint mCpbForegroundPaint;
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
        mCpbWholeBackgroundColor = a.getColor(R.styleable.CircleProgressBar_cpbWholeBackgroundColor, mCpbWholeBackgroundColor);
        mCpbForegroundColor = a.getColor(R.styleable.CircleProgressBar_cpbForegroundColor, mCpbForegroundColor);
        mCpbBackgroundColor = a.getColor(R.styleable.CircleProgressBar_cpbBackgroundColor, mCpbBackgroundColor);
        mCpbProgressTextColor = a.getColor(R.styleable.CircleProgressBar_cpbProgressTextColor, mCpbProgressTextColor);
        mCpbStrokeWidth = a.getInt(R.styleable.CircleProgressBar_cpbStrokeWidth, mCpbStrokeWidth);
        mCpbStartAngle = a.getInt(R.styleable.CircleProgressBar_cpbStartAngle, mCpbStartAngle);
        mCpbMaxAngle = a.getInt(R.styleable.CircleProgressBar_cpbMaxAngle, mCpbMaxAngle);
        mCpbNeedShowText = a.getBoolean(R.styleable.CircleProgressBar_cpbNeedShowText, mCpbNeedShowText);
        mCpbNeedAnim = a.getBoolean(R.styleable.CircleProgressBar_cpbNeedAnim, mCpbNeedAnim);

        mCpbNeedAnim = mCpbMaxAngle % 360 == 0 && mCpbNeedAnim;
        mCpbProgressTextColor = mCpbProgressTextColor == 0 ? mCpbForegroundColor : mCpbProgressTextColor;
        a.recycle();
    }

    private void init() {
        initWholeBackgroundPaint();
        initProgressBackgroundPaint();
        initProgressForegroundPaint();
        initTextPaint();
        initRectF();
        initAnim();
    }

    // init progress background paint
    private void initWholeBackgroundPaint() {
        mCpbWholeBackgroundPaint = new Paint();
        mCpbWholeBackgroundPaint.setAntiAlias(true);
        mCpbWholeBackgroundPaint.setColor(mCpbWholeBackgroundColor);
    }

    // init progress background paint
    private void initProgressBackgroundPaint() {
        mCpbBackgroundPaint = new Paint();
        mCpbBackgroundPaint.setAntiAlias(true);
        mCpbBackgroundPaint.setStyle(Paint.Style.STROKE);
        mCpbBackgroundPaint.setStrokeWidth(mCpbStrokeWidth);
        mCpbBackgroundPaint.setColor(mCpbBackgroundColor);
    }

    // init progress foreground paint
    private void initProgressForegroundPaint() {
        mCpbForegroundPaint = new Paint();
        mCpbForegroundPaint.setAntiAlias(true);
        mCpbForegroundPaint.setDither(true);
        mCpbForegroundPaint.setStyle(Paint.Style.STROKE);
        mCpbForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mCpbForegroundPaint.setStrokeWidth(mCpbStrokeWidth);
        mCpbForegroundPaint.setColor(mCpbForegroundColor);
    }

    // init text paint
    private void initTextPaint() {
        mCpbTextPaint = new Paint();
        mCpbTextPaint.setAntiAlias(true);
        mCpbTextPaint.setColor(mCpbProgressTextColor);
    }

    // init rectF
    private void initRectF() {
        mBackgroundRectF = new RectF();
        mForegroundRectF = new RectF();
        mWholeRectF = new RectF();
    }

    // init Animation Runnable
    private void initAnim() {
        if (mCpbNeedAnim) {
            mAnimRunnable = new AnimRunnable();
        }
    }

    // init shader
    private void initShader() {
        Shader colorShader = null;
        // init color shader
        if (mColorScheme != null && mColorScheme.length != 0) {
            float end = mMinWidth - mCpbStrokeWidth / 2;
            colorShader = new LinearGradient(0, 0, end, end, mColorScheme, null,
                    Shader.TileMode.CLAMP);
        }

        // set shader
        if (colorShader != null) {
            setShader(colorShader);
        }
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
            mBackgroundRectF.set(rLeft, rTop, rRight, rBottom);
            mForegroundRectF.set(rLeft, rTop, rRight, rBottom);
            mWholeRectF.set(mCpbStrokeWidth / 2, mCpbStrokeWidth / 2, mMinWidth - mCpbStrokeWidth / 2, mMinWidth - mCpbStrokeWidth / 2);
            initShader();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCpbWholeBackground(canvas);
        drawCpbBackground(canvas);
        drawCpbForeground(canvas);
        drawProgressText(canvas);
    }


    private void drawCpbWholeBackground(Canvas canvas) {
        canvas.drawCircle(mForegroundRectF.centerX(), mForegroundRectF.centerY(), mForegroundRectF.height() / 2, mCpbWholeBackgroundPaint);
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

    private void drawCpbForeground(Canvas canvas) {
        int startAngle = mAngleStep + mCpbStartAngle;
        int sweepAngle = (int) ((mCurrentProgress / MAX_PROGRESS) * mCpbMaxAngle);
        canvas.drawArc(mForegroundRectF, startAngle, sweepAngle, false, mCpbForegroundPaint);
    }

    private void drawCpbBackground(Canvas canvas) {
        canvas.drawArc(mBackgroundRectF, mCpbStartAngle, mCpbMaxAngle, false, mCpbBackgroundPaint);
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

    private void setShader(Shader shader) {
        mCpbForegroundPaint.setShader(shader);
        if (mCpbProgressTextColor != mCpbForegroundColor || !mCpbNeedShowText) return;
        mCpbTextPaint.setShader(shader);
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

