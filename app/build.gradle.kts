plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.example.loginpage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.loginpage"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
   // implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
 //   implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    //   implementation("com.google.firebase:firebase-database-ktx:20.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.material:material:1.11.0")//AppCompact Button
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")  //RecyclerView
    implementation ("nl.joery.animatedbottombar:library:1.1.0") //Bottom Navigation Bar
 //   implementation(platform("com.google.firebase:firebase-bom:32.8.0")) //firebase
 //   implementation("com.google.firebase:firebase-analytics")//firebase
 //   implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.takisoft.datetimepicker:datetimepicker:1.0.3")//date &time picker
 //   implementation ("com.google.firebase:firebase-storage-ktx:20.3.0")//firebase storage
    implementation ("com.github.bumptech.glide:glide:4.12.0")//getting imagefrome fire store
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.12.0")//getting imagefrome fire store
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")//getting imagefrome fire store
}