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
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 线形进度条
 * Created by ted on 4/17/15.
 */
public class LineProgressBar extends View {

    private static final String KEY_INSTANCE_STATE = "instance_state";
    private static final String KEY_STATE_CURRENT_PROGRESS = "state_current_progress";
    private static final String KEY_STATE_NEED_SHOW_TEXT = "state_need_show_text";
    private static final String KEY_STATE_NEED_ANIM = "state_need_anim";
    private static final String KEY_STATE_ORIENTATION = "state_orientation";

    private static final float MAX_PROGRESS = 100f;

    private int mLpbBackgroundColor = Color.BLACK;
    private Bitmap mLpbProgressImage, mLpbSecondaryProgressImage;
    private int mLpbProgressColor = Color.RED;
    private int mLpbProgressTextColor = Color.WHITE;
    private boolean mLpbNeedShowText = true;
    private boolean mLpbNeedAnim = false;
    private float mLpbImageWidth = -1, mLpbImageHeight = -1;
    private Orientation mLpbOrientation = Orientation.horizontal; // default is horizontal

    private Context mContext;
    private Paint mBackgroundPaint, mProgressPaint, mTextPaint;
    private int mCurrentProgress = 0;
    private int mMinWidth;
    private int mMaxWidth;

    private enum Orientation {
        horizontal(0), vertical(1);
        private int value;

        private Orientation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public LineProgressBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LineProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        parseAttributes(attrs);
        init();
    }

    private void parseAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LineProgressBar);
        mLpbBackgroundColor = a.getColor(R.styleable.LineProgressBar_lpbBackgroundColor, mLpbBackgroundColor);
        mLpbProgressColor = a.getColor(R.styleable.LineProgressBar_lpbProgressColor, mLpbProgressColor);
        mLpbProgressTextColor = a.getColor(R.styleable.LineProgressBar_lpbProgressTextColor, mLpbProgressTextColor);
        mLpbNeedShowText = a.getBoolean(R.styleable.LineProgressBar_lpbNeedShowText, mLpbNeedShowText);
        mLpbNeedAnim = a.getBoolean(R.styleable.LineProgressBar_lpbNeedAnim, mLpbNeedAnim);
        mLpbImageWidth = a.getDimension(R.styleable.LineProgressBar_lpbImageWidth, mLpbImageWidth);
        mLpbImageHeight = a.getDimension(R.styleable.LineProgressBar_lpbImageHeight, mLpbImageHeight);
        int orientation = a.getInteger(R.styleable.LineProgressBar_lpbOrientation, Orientation.horizontal.getValue());
        BitmapDrawable progressImage = (BitmapDrawable) a.getDrawable(R.styleable.LineProgressBar_lpbProgressImage);
        BitmapDrawable secondImage = (BitmapDrawable) a.getDrawable(R.styleable.LineProgressBar_lpbSecondaryProgressImage);
        a.recycle();

        mLpbOrientation = orientation == Orientation.horizontal.getValue()
                ? Orientation.horizontal : Orientation.vertical;
        mLpbProgressImage = progressImage != null ? progressImage.getBitmap() : null;
        mLpbSecondaryProgressImage = secondImage != null ? secondImage.getBitmap() : null;
        resizeImageIfNeed();
    }

    private void init() {
        initBackgroundPaint();
        initProgressPaint();
        initTextPaint();
    }

    private void initBackgroundPaint() {
        mBackgroundPaint = initImagePaint(mLpbSecondaryProgressImage, mLpbBackgroundColor);
    }

    private void initProgressPaint() {
        mProgressPaint = initImagePaint(mLpbProgressImage, mLpbProgressColor);
    }

    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mLpbProgressTextColor);
    }

    private Paint initImagePaint(Bitmap bmp, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        if (bmp != null) {
            BitmapShader progressShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(progressShader);
        } else {
            paint.setColor(color);
        }
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLpbProgressImage != null) {
            setMeasuredDimension(mLpbProgressImage.getWidth(), mLpbProgressImage.getHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed) return;
        mMaxWidth = getWidth();
        int height = getHeight();
        if (mLpbProgressImage != null) {
            mMaxWidth = Math.max(mMaxWidth, mLpbProgressImage.getWidth());
            height = Math.max(height, mLpbProgressImage.getHeight());
        }
        mMinWidth = Math.min(mMaxWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawLineProgress(canvas);
        drawProgressText(canvas);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        state.putInt(KEY_STATE_CURRENT_PROGRESS, mCurrentProgress);
        state.putBoolean(KEY_STATE_NEED_SHOW_TEXT, mLpbNeedShowText);
        state.putBoolean(KEY_STATE_NEED_ANIM, mLpbNeedAnim);
        state.putInt(KEY_STATE_ORIENTATION, mLpbOrientation.getValue());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentProgress = bundle.getInt(KEY_STATE_CURRENT_PROGRESS);
            mLpbNeedAnim = bundle.getBoolean(KEY_STATE_NEED_ANIM);
            mLpbNeedShowText = bundle.getBoolean(KEY_STATE_NEED_SHOW_TEXT);
            int orientation = bundle.getInt(KEY_STATE_ORIENTATION, 0);
            mLpbOrientation = orientation == Orientation.vertical.getValue()
                    ? Orientation.vertical : Orientation.horizontal;
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void drawBackground(Canvas canvas) {
        // todo add default color filter
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);
    }

    private void drawLineProgress(Canvas canvas) {
        float rate = mCurrentProgress / MAX_PROGRESS;
        float startX = 0;
        float startY = mLpbOrientation == Orientation.vertical ? getHeight() : 0;
        float stopX = mLpbOrientation == Orientation.horizontal ? getWidth() * rate : getWidth();
        float stopY = mLpbOrientation == Orientation.vertical ? getHeight() * (1 - rate) : getHeight();
        canvas.drawRect(startX, startY, stopX, stopY, mProgressPaint);
    }

    private void drawProgressText(Canvas canvas) {
        if (mLpbProgressImage != null || !mLpbNeedShowText) return;
        String text = String.valueOf(mCurrentProgress);
        int textSize = (int) (mMinWidth / 2.5);
        mTextPaint.setTextSize(textSize);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        float textWidth = mTextPaint.measureText(text);
        float x = (mMaxWidth - textWidth) / 2;
        float y = (mMinWidth - textHeight) / 2 + textSize;
        canvas.drawText(text, x, y, mTextPaint);
    }

    private void resizeImageIfNeed() {
        if (mLpbProgressImage == null || mLpbImageWidth == -1 || mLpbImageHeight == -1) return;
        mLpbProgressImage = resizeImage(mLpbProgressImage);
        mLpbSecondaryProgressImage = mLpbSecondaryProgressImage == null
                ? null : resizeImage(mLpbSecondaryProgressImage);
    }

    private Bitmap resizeImage(Bitmap bmp) {
        return Bitmap.createScaledBitmap(bmp,
                (int) mLpbImageWidth, (int) mLpbImageHeight, false);
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
        if (!mLpbNeedAnim) {
            invalidateUi();
        }
    }
}
