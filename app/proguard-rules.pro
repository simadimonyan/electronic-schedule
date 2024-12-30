# General Android settings
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations (for libraries like Gson, Retrofit, Room, etc.)
-keepattributes *Annotation*
-keepattributes Signature, InnerClasses

# Keep application model classes (serialization/deserialization)
-keep class com.myapplication.model.** { *; }

# For Gson (JSON serialization/deserialization)
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepattributes AnnotationDefault, RuntimeVisibleAnnotations

# Keep classes extending TypeToken (for generic handling in Gson)
-keep class * extends com.google.gson.reflect.TypeToken

# For Jetpack Compose (UI Framework)
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# For Coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Optional: Hide source file names to improve obfuscation
# -renamesourcefileattribute ObfuscatedFile

# Prevent stripping of enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Prevent stripping of Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Optional: For debugging stack traces
# Uncomment the following to keep method names intact (useful for debugging but reduces obfuscation)
-keep class com.imsit.schedule.** { *; }
-keepattributes SourceFile,LineNumberTable
-keep class * {
    public *;
}

# Optional: Exclude logging libraries like Timber from obfuscation
-keep class timber.log.** { *; }
-dontwarn timber.log.**

# Exclude generated code from being stripped (like Dagger, Hilt, etc.)
-keep class dagger.** { *; }
-dontwarn dagger.**
-keep class javax.inject.** { *; }
-dontwarn javax.inject.**

# Optional: For Kotlin-specific classes
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }
-dontwarn kotlin.**

# Exclude test packages (optional)
-dontwarn com.myapplication.tests.**
-keep class com.myapplication.tests.** { *; }
