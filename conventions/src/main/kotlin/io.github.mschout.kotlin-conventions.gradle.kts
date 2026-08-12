import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// This plugin ID becomes: mschout.kotlin-conventions
// Apply it in a consuming project with: id("mschout.kotlin-conventions")

plugins { org.jetbrains.kotlin.jvm }

repositories { mavenCentral() }

val toolchainVersion = providers.gradleProperty("jvmToolchainVersion").getOrElse("21").toInt()

val jvmTargetVersion = providers.gradleProperty("jvmTarget").getOrElse("$toolchainVersion")

kotlin {
  jvmToolchain(toolchainVersion)
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
    jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion))
  }
}

// Keep javac's target in sync with Kotlin's jvmTarget when it is lower than the toolchain,
// otherwise the Kotlin plugin's JVM-target validation fails the build.
tasks.withType<JavaCompile>().configureEach { options.release.set(jvmTargetVersion.toInt()) }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    allWarningsAsErrors.set(false) // flip to true when you're ready
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

dependencies {
  testImplementation(platform("org.junit:junit-bom:6.1.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
