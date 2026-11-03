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

