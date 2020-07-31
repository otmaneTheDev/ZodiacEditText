# ZodiacEditText
[![](https://jitpack.io/v/otmaneTheDev/ZodiacEditText.svg)](https://jitpack.io/#otmaneTheDev/ZodiacEditText)

### Inspiration
This project was inspired in https://dribbble.com/shots/9110546-Birthday-input-field component design.

![](https://cdn.dribbble.com/users/414694/screenshots/9110546/media/3b60dd0bed980790ac0f5bcaeeedce16.mp4)

### Sample project
View the sample app's source code [here](https://github.com/otmaneTheDev/ZodiacEditText/tree/master/app)

### Setup

Add the JitPack repository to your project level `build.gradle`:
```groovy
allprojects {
 repositories {
    google()
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
Add ZodiacEditText to your app `build.gradle`:
```groovy
dependencies {
    implementation 'com.github.otmaneTheDev:ZodiacEditText:<latest-version>'
}
```

## Usage

Add ZodiacEditText to your XML like any other view.

```xml
    <otmanethedev.zodiacedittext.ZodiacEditText
        android:id="@+id/zodiacEditText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/margin_default"
        app:defaultColor="@color/customColorBlue"
        app:errorColor="@color/customColorRed"
        app:hintColor="@color/customColorGrey"
        app:textColor="@color/customColorBlack" />
```

Set ZodiacEditText DateChangedListener.

```java
        zodiacEditText.setDateChangedListener(object :ZodiacEditText.DateChangedListener{
            override fun onDateChanged(date: Date) {
                // Do your stuff here
            }
        })
```

### Contributing

Found a bug? feel free to fix it and send a pull request
