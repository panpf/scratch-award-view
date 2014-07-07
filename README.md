# ![Logo](https://github.com/xiaopansky/RubberView/raw/master/app/src/main/res/drawable-mdpi/ic_launcher.png) RubberView

这是Android上的一个涂抹视图，用于实现刮奖效果

![Sample](https://github.com/xiaopansky/RubberView/raw/master/docs/sample.png)

##Usage guide
你只需将RubberView覆盖在中奖提示语之上即可，这样的用法很灵活，因此你可以决定你的中奖提示语是一段文字或者一张图片，如下所示：
```xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_centerInParent="true">
    <TextView
        android:text="再来一瓶"
        android:gravity="center"
        android:layout_width="200dp"
        android:layout_height="100dp"
        android:layout_gravity="center"/>

    <me.xiaopan.android.rubberview.RubberView
        android:id="@+id/rubberView_main"
        android:layout_width="200dp"
        android:layout_height="100dp"
        android:layout_gravity="center"/>
</FrameLayout>
```
扩展功能：
>* 调用setMaskImage()方法自定义遮罩图片，默认的是灰色
>* 调用setStrokeWidth()方法自定义画笔宽度

##Downloads
>* [android-rubber-view-1.0.0.jar](https://github.com/xiaopansky/RubberView/raw/master/releases/android-rubber-view-1.0.0.jar)
>* [android-rubber-view-1.0.0-with-src.jar](https://github.com/xiaopansky/RubberView/raw/master/releases/android-rubber-view-1.0.0-with-src.jar)

##License
```java
/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
