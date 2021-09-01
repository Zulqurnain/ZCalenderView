# ZCalenderView

It's a custom calender inspired from iOS7 style calender , displays Months verticaly. you can include this library using gradle , maven and if you enjoy don't forget to follow me on facebook [@RajaJutt](https://www.facebook.com/Raja.jutt "joine on facebook") , twitter [@zulqurnainbro](https://twitter.com/zulqurnainbro "twitter") Thanks ;)

## ScreenShots

<img src="https://github.com/Zulqurnain/ZCalenderView/raw/master/screenshots/1.png" width="200"> <img src="https://github.com/Zulqurnain/ZCalenderView/raw/master/screenshots/2.png" width="200"> <img src="https://github.com/Zulqurnain/ZCalenderView/raw/master/screenshots/3.png" width="200">


## Features:
- Support Up to API level 11+
- Show Months Verticaly 
- Support for Marking Events 
- Curreent day and Marked day selection color and text changing
- fixed crashes

### Gradle [![](https://jitpack.io/v/Zulqurnain/ZCalenderView.svg)](https://jitpack.io/#Zulqurnain/ZCalenderView)

**Step 1** Add the JitPack repository to your build file. Add it in your build.gradle at the end of repositories.

```java
  repositories {
    maven { url "https://jitpack.io" }
  }
```

**Step-2** Add the dependency in the form

```java
dependencies {
    implement 'com.github.Zulqurnain:ZCalenderView:v2.0'
}
```
### Maven
```xml
<repository>
     <id>jitpack.io</id>
     <url>https://jitpack.io</url>
</repository>
```
**Step 2** Add the dependency in the form
```xml
<dependency>
	    <groupId>com.github.Zulqurnain</groupId>
	    <artifactId>ZCalenderView</artifactId>
	    <version>v1.0</version>
</dependency>
```
### Sbt
**Step-1** Add it in your build.sbt at the end of resolvers:
```java
resolvers += "jitpack" at "https://jitpack.io"
```
**Step-2** Add the dependency in the form
```java
	libraryDependencies += "com.github.Zulqurnain" % "ZCalenderView" % "v1.0"	
```

### Usage
 
Declare a ZCalenderView inside your layout XML file:
 
``` xml

  <jutt.com.zcalenderview.ZCalenderView
        android:id="@+id/calendar_view"
        xmlns:calendar="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/ll_calender_month"
        android:background="@android:color/white"
        calendar:colorMonthName="@color/colorGreen"
        calendar:colorCurrentDayCircle="@color/colorLightRed"
        calendar:colorCurrentDayText="@android:color/white"
        calendar:calendarHeight="400dp"
        calendar:colorDayName="@color/colorPrimary"
        calendar:colorNormalDay="@color/colorPrimary"
        calendar:drawRoundRect="false"/>
         
```

Next, you have to implement `DatePickerController` in your Activity or your Fragment. calender originaly shows 3 years data current year previous one and next one

``` java

    @Override
    public void onDayOfMonthSelected(int year, int month, int day)
    {
        Log.e("Day Selected", day + " / " + month + " / " + year);
    }
    
```

Then you can use it like this:

```
  calenderView = (ZCalenderView) findViewById(R.id.calendar_view);

  // on click of specific date
  calenderView.setController(this);

  // define if you want to show 1 month for this view only and vertical scroll is enable in this case
  calenderView.setEnableHeightResize(false);

  // events require hashmap
  HashMap<SimpleMonthAdapter.CalendarDay,Integer> eventsMap = new HashMap<>();

  // this date is basically 1st august 2017 and it will show 1 event dot under this day
  eventsMap.put(new SimpleMonthAdapter.CalendarDay(2017,7,20),1);

  calenderView.setEventsHashMap(eventsMap);

```

---

### Customization

ZCalenderView is fully customizable:

* app:colorSelectedDayBackground  --> If you click on a day, a circle indicator or a rouded rectangle indicator will be draw.
* app:colorSelectedDayText  --> This is the text color of a selected day
* app:colorPreviousDay [color def:#ff999999] --> In the current month you can choose to have a specific color for the past days
* app:colorNormalDay [color def:#ff999999] --> Default text color for a day
* app:colorMonthName [color def:#ff999999] --> Month name and year text color (three letter words)
* app:colorDayName [color def:#ff999999] --> Day name text color
* app:textSizeDay [dimension def:16sp] --> Font size for numeric day must be greater than or equal to given
* app:drawRoundRect [boolean def:false] --> Draw a rounded rectangle for selected days instead of a circle
* app:selectedDayRadius [dimension def:16dip] --> Set radius if you use default circle indicator
* app:calendarHeight [dimension def:270dip] --> Height of each month/row
* app:enablePreviousDay [boolean def:true] --> Enable past days in current month
* app:currentDaySelected [boolean def:false] --> Select current day by default
* app:colorCurrentDayCircle --> current day circle color
* app:colorCurrentDayText --> current day text color

### Acknowledgements

Thanks to:
- [Robin Chutaux](https://github.com/traex) for his [CalendarListview](https://github.com/traex/CalendarListview).
- [JunGuan Zhu](https://github.com/NLMartian) for further improving Robin's work as [SilkCal](https://github.com/NLMartian/SilkCal)

### MIT License

```
    The MIT License (MIT)
    
    Copyright (c) 2014 Junguan Zhu
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
```
