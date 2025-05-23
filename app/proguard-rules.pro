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

# Keep the PDF viewer classes
-keep class com.github.barteksc.pdfviewer.** { *; }

# Keep the ExoPlayer classes
-keep class androidx.media3.** { *; }

# Keep the Kotlin Serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep the DataStore classes
-keep class androidx.datastore.** { *; }

# Keep the Compose classes
-keep class androidx.compose.** { *; }

# Keep the Navigation classes
-keep class androidx.navigation.** { *; }

# Keep the Coil classes
-keep class coil.** { *; }