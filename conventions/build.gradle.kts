plugins {
  `kotlin-dsl`
  alias(libs.plugins.spotless)
}

repositories {
  gradlePluginPortal()
  mavenCentral()
}

val javaVersion = providers.gradleProperty("jvmToolchainVersion").getOrElse("21").toInt()
kotlin { jvmToolchain(javaVersion) }

dependencies {
  // These are the plugins your convention plugins will apply.
  // Declare them here as dependencies so they're available at configuration time.
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.spotless.gradle.plugin)
  implementation(libs.version.catalog.update.gradle.plugin)
  implementation(libs.git.version.gradle.plugin)

  // not applied by default but made available
  implementation(libs.gradle.maven.publish.plugin)
  implementation(libs.dokka.gradle.plugin)
}

spotless {
  kotlinGradle {
    target("*.gradle.kts", "**/*.gradle.kts")
    ktfmt()
  }
}
