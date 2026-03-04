# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep ExoPlayer classes
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }

# Keep model classes
-keep class com.jbworks.radiowidget.model.** { *; }
