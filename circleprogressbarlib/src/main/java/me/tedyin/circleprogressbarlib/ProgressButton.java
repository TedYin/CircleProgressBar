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
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 进度按钮
 * Created by ted on 4/17/15.
 */
public class ProgressButton extends ViewGroup{

    private Context mContext;

    public ProgressButton(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        parseAttributes(attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void parseAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        a.recycle();
    }

    private void init() {

    }


}
