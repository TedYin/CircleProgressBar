# CircleProgressBar
An android ProgressBar widget .

[ ![Download](https://api.bintray.com/packages/tedyin/maven/CircleProgressBar/images/download.svg) ](https://bintray.com/tedyin/maven/CircleProgressBar/_latestVersion)

## Demo
![loading](https://tedyin.github.io/images/loading.gif)

# Gradle
```
compile 'me.tedyin.circleprogressbarlib:circleprogressbarlib:0.6.1'
```

# Usage
## Layout xml

CircleProgressBar
```xml
<me.tedyin.circleprogressbarlib.CircleProgressBar
	android:layout_width="56dp" 
	android:layout_height="56dp"
    app:cpbStrokeWidth="10"
    app:cpbBackgroundColor="#9000"
    app:cpbForegroundColor="#0b90cf"
    app:cpbProgressTextColor="#ffaabb"
    app:cpbWholeBackgroundColor="#ff5ca5c3"
    app:cpbNeedAnim="true"
    app:cpbStartAngle="-90"
    app:cpbMaxAngle="360"
    app:cpbNeedShowText="true"/>
```

LineProgressBar
```xml
 <me.tedyin.circleprogressbarlib.LineProgressBar
    android:layout_gravity="center"
    android:layout_height="wrap_content"
    android:background="@android:color/transparent"
    app:lpbProgressImage="@mipmap/icon"
    app:lpbSecondaryProgressImage="@mipmap/icon_black"
    android:layout_width="wrap_content"
    app:lpbImageWidth="100dp"
    app:lpbImageHeight="100dp" />

```

## Java Method:
```java
CircleProgressBar bar;
...

// set color scheme
bar.setColorScheme(Color.GREEN, Color.YELLOW, Color.RED);

// Loading complete callback
bar.setLoadingCallBack(new CircleProgressBar.LoadingCallBack() {
    @Override
    public void loadingComplete(View v) {
        // do loading complete
    }
});
```
# Update log
+ v0.6.1 添加背景颜色设置属性,修改进度颜色属性名称
+ v0.6.0 添加横向进度条

# License
> Copyright 2015 TedYin

> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
> 
>    http://www.apache.org/licenses/LICENSE-2.0
> 
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.