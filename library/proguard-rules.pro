# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontusemixedcaseclassnames

-keep class com.sfmap.api.location.SfMapLocation {
    public <methods>;
}

-keep class com.sfmap.api.location.SfMapLocationClient {
    public <methods>;
    public *;
}

-keep class com.sfmap.api.location.SfMapLocationClientOption {
    public <methods>;
}

-keep class com.sfmap.api.location.SfMapLocationListener {
    public <methods>;
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
#-keepattributes Signature

# Gson specific classes
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.sfmap.api.location.client.bean.** { *; }

-keep public enum com.sfmap.api.location.SfMapLocationClientOption$** {
    **[] $VALUES;
    public *;
}


