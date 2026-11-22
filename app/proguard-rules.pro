# Muslim Link Production ProGuard / R8 Rules

# Keep data models, entities, and serializers
-keep class com.mosque.community.data.models.** { *; }
-keep class com.mosque.community.data.local.entity.** { *; }
-keep class com.mosque.community.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Kotlin reflection & Coroutines
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.** { *; }

# Coil image loading
-keep class coil.** { *; }
-dontwarn coil.**

# Compose rules
-keep class androidx.compose.material.icons.** { *; }
