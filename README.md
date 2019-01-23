# SwipeBack
Finish the activity by using a swipe-right gesture.


Demo
======

<img src="gifs/sample_1_0_1.gif" width="246">


Methods
===

| Name | Return | Definition |
| :---:   | :-:  | :-:  |
| getShadowColor() | Int | Setting the color of shadow. |
| getLaunchAnimation() | Boolean | Setting the switch of launch-activity animation. |
| abstract getDirection() | Direction | Setting the direction of the swiping gesture. |
| onSwiped(float persent, float position) | void | Interface definition for a callback to be invoked when a gesture event is dispatched to this view.|

Sample
======

```
public class SampleActivity extends SwipeBaseActivity {
    @NotNull
    @Override
    public SwipeBaseActivity.Direction getDirection() {
        return SwipeBaseActivity.Direction.RIGHT;
    }

    @Override
    protected int getShadowColor() {
        return Color.parseColor("#74000000");
    }
}
```

Download
========
```xml
<dependency>
  <groupId>com.yangping</groupId>
  <artifactId>swipeback</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
or Gradle ( jcenter ):
```groovy
implementation 'com.yangping:swipeback:1.0.0'
```


### License
```
Copyright 2019 zih-yang lin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```