plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "dev.lumentv"
    compileSdk = 35
    defaultConfig {
        applicationId = "dev.lumentv"
        minSdk = 23
        targetSdk = 35
        versionCode = 7
        versionName = "0.7.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_1_8; targetCompatibility = JavaVersion.VERSION_1_8 }
    kotlinOptions { jvmTarget = "1.8" }
    lint { abortOnError = true }
}
dependencies {
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
}
