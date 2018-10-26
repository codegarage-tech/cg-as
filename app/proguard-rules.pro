# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/suzuki.ren/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

# Annotation, Exceptions, Signatureなどを難読化対象から外す

-keepattributes *Annotation*,Exceptions,Signature,SourceFile,LineNumberTable,InnerClass

# Activity, Application, Service, BroadcastReceiver等
# Androidシステム上難読化できないクラスは除外

-keep public class * extends android.app.*
-keep public class * extends android.content.*
-keep public class * extends android.os.Binder
-keep public class * extends android.widget.*
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.app.AppCompatActivity

-keep public class * extends com.nttdocomo.dch.base.BaseActivity
-keep public class * extends com.nttdocomo.dch.base.BaseFragment
-keep public class * extends com.nttdocomo.dch.base.mvp.BasePresenter
-keep public class * extends com.nttdocomo.dch.base.mvp.BaseView


-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-dontwarn android.security.**
-dontwarn android.databinding.**

########## Android Support Library ##########
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

########## OkHttp3 ##########
-keep class okhttp3.** { *; }
-keep interface okhttp3.* { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

########## SQLite ##########
-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }

########## Gson ##########
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class sum.misc.Unsafe { *; }
-keepattributes Expose
-keepattributes SerializedName
-keepattributes Since
-keepattributes Until
-keepclasseswithmembers class * { @com.google.gson.annotations.Expose <fields>; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# keep enum so gson can deserialize it
-keepclassmembers enum * { *; }

### TODO declare the classes which use GSON
-keep class com.onecodelabs.reminder.remindful.SqliteRemindfulPersister {*;}
-keep class com.onecodelabs.reminder.util.** {*;}
-keep class com.rc.abovesound.model.** {*;}

########## Apache commons ##########
-keep class org.apache.commons.** {*;}