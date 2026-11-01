# Squircle IDE uses reflection to access some DrawerLayout fields
-keepclassmembernames class androidx.drawerlayout.widget.DrawerLayout {
    private androidx.customview.widget.ViewDragHelper mLeftDragger;
}
-keepclassmembernames class androidx.customview.widget.ViewDragHelper {
    private int mEdgeSize;
}
# Huawei Ads (hms flavor)
-keep class com.huawei.openalliance.ad.** { *; }
-keep class com.huawei.hms.ads.** { *; }
-dontwarn com.huawei.**
