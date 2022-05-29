import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
}

group = "dev.kamilchulakov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val javaFXVersion = "18"
val lets_plot_version = "2.3.0"
val javaFXPlatform = "linux"
val lets_plot_kotlin_version="3.2.0"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.lets-plot:lets-plot-jfx:$lets_plot_version")
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:$lets_plot_kotlin_version")
    implementation("org.openjfx:javafx-base:$javaFXVersion:$javaFXPlatform")
    implementation("org.openjfx:javafx-swing:$javaFXVersion:$javaFXPlatform")
    implementation("org.openjfx:javafx-graphics:$javaFXVersion:$javaFXPlatform")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}