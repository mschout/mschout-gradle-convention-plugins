import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// This plugin ID becomes: mschout.kotlin-conventions
// Apply it in a consuming project with: id("mschout.kotlin-conventions")

plugins { org.jetbrains.kotlin.jvm }

group = "com.example"

repositories { mavenCentral() }

val javaVersion = providers.gradleProperty("jvmToolchainVersion").getOrElse("21").toInt()

val jvmTargetVersion = providers.gradleProperty("jvmTarget").getOrElse("$javaVersion")

kotlin {
  jvmToolchain(javaVersion)
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
    jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion))
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    allWarningsAsErrors.set(false) // flip to true when you're ready
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.11.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
