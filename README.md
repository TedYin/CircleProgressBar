# CircleProgressBar
An android ProgressBar widget .

# Usage
Layout xml
```xml
<me.tedyin.circleprogressbarlib.CircleProgressBar
	android:layout_width="56dp" 
	android:layout_height="56dp"
    app:cpbStrokeWidth="10"
    app:cpbBackgroundColor="#9000"
    app:cpbProgressColor="#0b90cf"
    app:cpbProgressTextColor="#ffaabb"
    app:cpbNeedAnim="true"
    app:cpbStartAngle="-90"
    app:cpbMaxAngle="360"
    app:cpbNeedShowText="true"/>
```

Java Method:
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

# License
> Copyright 2015 TedYin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.