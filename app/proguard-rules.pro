# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/rashed/BattleField/Android_setup/Android_stuido_setup/sdk/tools/proguard/proguard-android.txt
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

#Default/common configuration for all project.
-optimizationpasses 5
-dontusemixedcaseclassnames
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-dontoptimize
-ignorewarning
-useuniqueclassmembernames
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-repackageclasses ''

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class android.graphics.Canvas

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Preserve all native method names and the names of their classes.
-keepclassmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public boolean performClick();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keep public class * {
    public protected *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * extends andriod.app.Activity {
    public void *On*Click(android.view.View);
    public void *on*Click(android.view.View);
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

##---------------End: proguard configuration common for all Android apps ----------

##--------------Start: Custom library projects and libs go here.
-keep class com.customviews.library.**{ *; }
-keep class com.customviewanimations.library.**{ *; }
-keep class com.android.volley.** { *; }
-keep class com.nineoldandroids.** { *; }
-keep class org.apache.** { *; }
-keep class com.android.internal.http.multipart.** { *; }
-keep class com.daimajia.easing.** { *; }

-keep class com.github.tamir7.contacts.** { *; }
-keep class com.diroag.floatingwindows.** { *; }
-keep class com.wdullaer.materialdatetimepicker.** { *; }
-keep class com.reversecoder.residemenu.** { *; }
-keep class com.rodolfonavalon.shaperipplelibrary.** { *; }
-keep class br.com.stickyindex.** { *; }
-keep class cc.solart.wave.** { *; }
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.reversecoder.ci.model.** { *; }
-keep class com.github.tamir7.contacts.** { *; }
##---------------End: proguard configuration for Gson  ----------