# Proguard rules for Garh-Seva (Nagar Parishad Chittorgarh)

# Preserve generic signatures & annotations for reflection and serialization
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Kotlinx Serialization
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    *** serializer(...);
}
-keep @kotlinx.serialization.Serializable class * { *; }

# Data Models
-keep class com.chittortech.snp.data.models.** { *; }
-keepclassmembers class com.chittortech.snp.data.models.** { *; }

# Supabase & Ktor
-keep class io.github.jan.supabase.** { *; }
-keepclassmembers class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**
-dontwarn io.github.jan.supabase.**

# Android Compose & Coroutines
-dontwarn kotlinx.coroutines.**

# Google ErrorProne annotations (not needed at runtime — suppress R8 missing class warning)
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn javax.lang.model.**
-dontwarn org.checkerframework.**

# OkHttp / Okio (used internally by Ktor / Supabase)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.slf4j.**

# Kotlin metadata & reflect
-dontwarn kotlin.reflect.**
-dontwarn kotlin.Unit

