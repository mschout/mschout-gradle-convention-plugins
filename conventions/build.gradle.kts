plugins {
  `kotlin-dsl`
  alias(libs.plugins.spotless)
  alias(libs.plugins.git.version)
  alias(libs.plugins.maven.publish)
}

repositories {
  gradlePluginPortal()
  mavenCentral()
}

val gitVersion = extra["gitVersion"] as groovy.lang.Closure<*>

group = "io.github.mschout"

version = gitVersion.call().toString()

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

mavenPublishing {
  // kotlin-dsl applies java-gradle-plugin, so the main artifact and a marker
  // publication for every precompiled script plugin (io.github.mschout.*) are
  // configured automatically. The marker groupId equals the plugin ID, so the
  // io.github.mschout.* prefix keeps every artifact under the published namespace.
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()

  coordinates("io.github.mschout", "gradle-convention-plugins", version.toString())

  pom {
    name.set("mschout Gradle convention plugins")
    description.set("Reusable Gradle convention plugins for Kotlin projects.")
    url.set("https://github.com/mschout/mschout-gradle-convention-plugins")
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
    developers {
      developer {
        id.set("mschout")
        name.set("Michael Schout")
        email.set("admin@schouttech.com")
      }
    }
    scm {
      url.set("https://github.com/mschout/mschout-gradle-convention-plugins")
      connection.set("scm:git:https://github.com/mschout/mschout-gradle-convention-plugins.git")
      developerConnection.set(
          "scm:git:ssh://git@github.com/mschout/mschout-gradle-convention-plugins.git"
      )
    }
  }
}

// Refuse to release a version derived from a dirty working tree to Maven Central.
val validateVersion =
    tasks.register("validateVersion") {
      doLast {
        val v = project.version.toString()
        if (v.endsWith("dirty")) {
          throw GradleException("Refusing to publish a dirty version: $v")
        }
      }
    }

tasks.withType<PublishToMavenRepository>().configureEach {
  if (name.endsWith("ToMavenCentralRepository")) {
    dependsOn(validateVersion)
  }
}
