# ZCalendarView

ZCalendarView is a custom calendar inspired by the iOS 7 style. It displays months vertically and supports event marking. 

## 📸 Screenshots

<img src="https://github.com/Zulqurnain/ZCalenderView/raw/master/screenshots/1.png" width="200"> <img src="https://github.com/Zulqurnain/ZCalenderView/raw/master/screenshots/2.png" width="200"> <img src="https://github.com/Zulqurnain/ZCalenderView/raw/master/screenshots/3.png" width="200">

## ✨ Features

- Supports API level 11+
- Displays months **vertically** 📅
- Event marking support 🔴
- Customizable colors for the **current day** and **marked days** 🎨
- Fixed crashes for better stability 🛠️
- **Kotlin Support** 💜
- **Android 11+ Support** 📱
- Removed **JCenter** dependency 🚀

## 🔗 Installation

### Gradle (via JitPack) [![](https://jitpack.io/v/Zulqurnain/ZCalenderView.svg)](https://jitpack.io/#Zulqurnain/ZCalenderView)

**Step 1:** Add JitPack to your `build.gradle` repositories:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

**Step 2:** Add the dependency:

```gradle
dependencies {
    implementation 'com.github.Zulqurnain:ZCalenderView:2.0'
}
```

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.github.Zulqurnain</groupId>
    <artifactId>ZCalenderView</artifactId>
    <version>2.0</version>
</dependency>
```

## 🛠️ Usage

### XML Layout

```xml
<jutt.com.zcalenderview.ZCalenderView
    android:id="@+id/calendar_view"
    xmlns:calendar="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/white"
    calendar:colorMonthName="@color/colorGreen"
    calendar:colorCurrentDayCircle="@color/colorLightRed"
    calendar:colorCurrentDayText="@android:color/white"
    calendar:calendarHeight="400dp"
    calendar:colorDayName="@color/colorPrimary"
    calendar:colorNormalDay="@color/colorPrimary"
    calendar:drawRoundRect="false" />
```

### Implement `DatePickerController`

```java
@Override
public void onDayOfMonthSelected(int year, int month, int day) {
    Log.e("Day Selected", day + " / " + month + " / " + year);
}
```

### Set Up in Activity/Fragment

```java
calenderView = findViewById(R.id.calendar_view);
calenderView.setController(this);
calenderView.setEnableHeightResize(false);

// Add events
HashMap<SimpleMonthAdapter.CalendarDay, Integer> eventsMap = new HashMap<>();
eventsMap.put(new SimpleMonthAdapter.CalendarDay(2017, 7, 20), 1);
calenderView.setEventsHashMap(eventsMap);
```

## 🎨 Customization

| Attribute                 | Description |
|---------------------------|-------------|
| `app:colorSelectedDayBackground` | Background color of selected day |
| `app:colorSelectedDayText` | Text color of selected day |
| `app:colorPreviousDay` | Color of past days |
| `app:colorNormalDay` | Default text color for days |
| `app:colorMonthName` | Color of month name |
| `app:colorDayName` | Color of day names |
| `app:textSizeDay` | Font size for day numbers |
| `app:drawRoundRect` | Draws rounded rectangle for selection |
| `app:selectedDayRadius` | Radius of selection circle |
| `app:calendarHeight` | Height of calendar view |

## 📢 Follow Me

- **GitHub**: [github.com/Zulqurnain](https://github.com/Zulqurnain)
- **Twitter**: [@zulqurnainjj](https://x.com/zulqurnainjj)
- **TikTok**: [@zulqurnainjj](https://tiktok.com/@zulqurnainjj)
- **Facebook**: [@Raja.jutt](https://facebook.com/Raja.jutt)
- **YouTube**: [@zulqurnainjj](https://www.youtube.com/@zulqurnainjj)

🚀 Enjoy using ZCalendarView? Star ⭐ this repo and share your feedback!
