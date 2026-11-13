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

# AndroidX WorkManager (Worker reflection instantiations)
-keep class androidx.work.** { *; }
-keepclassmembers class androidx.work.** { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class com.chittortech.snp.util.SnpBackgroundSyncWorker { *; }

# AndroidX Security / EncryptedSharedPreferences / Tink
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }
-dontwarn androidx.security.crypto.**
-dontwarn com.google.crypto.tink.**

# Application & ViewModel classes
-keep class com.chittortech.snp.SnpApplication { *; }
-keep class com.chittortech.snp.MainActivity { *; }
-keep class com.chittortech.snp.viewmodel.** { *; }
-keepclassmembers class com.chittortech.snp.viewmodel.** { *; }


