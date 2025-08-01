// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // VERIFY AGP VERSION - "8.2.2" or "8.3.1" are examples of recent stable versions
    id("com.android.application") version "8.13.0" apply false // EXAMPLE - USE A VALID STABLE VERSION
    // Your Kotlin and KSP versions seem aligned for Kotlin 2.0/2.1 previews.
    // Ensure this Kotlin version is stable or what you intend for compatibility.
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false // EXAMPLE of latest stable Kotlin 1.9.x
    // OR if using Kotlin 2.0, "2.0.0"
    // Your "2.1.21" is very new (likely a dev/EAP build for Kotlin 2.1)
    // KSP "2.1.21-2.0.1" implies Kotlin 2.1.21 and KSP plugin 2.0.1
    // If you're on cutting edge, it's fine, but for stability, consider latest stable.

    id("com.google.dagger.hilt.android") version "2.50" apply false // Match Hilt version in module
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false // EXAMPLE KSP for Kotlin 1.9.23
    // OR if using Kotlin 2.0.0 -> ksp version "2.0.0-1.0.21"
    // Your "2.1.21-2.0.1"
}
