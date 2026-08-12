import io.github.mschout.gradle.ReleaseTask

plugins {
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.dokka")
}

// Tag and push a release: ./gradlew release -PreleaseVersion=x.y.z
// Then publish with: ./gradlew publishAndReleaseToMavenCentral (a separate
// invocation, so git-version resolves the version from the new tag).
tasks.register<ReleaseTask>("release") {
  group = "publishing"
  description = "Tags releaseVersion and pushes the branch and tags."
  releaseVersion.set(providers.gradleProperty("releaseVersion"))
  rootDir.set(project.rootDir)
}

val validateVersion =
    tasks.register("validateVersion") {
      doLast {
        val version = project.version.toString()
        if (version.endsWith("dirty")) {
          throw GradleException("Cannot publish a dirty version: $version")
        }
      }
    }

// Only enforce a clean version when publishing to remote repositories. The local
// "Private" file repository (build/maven-repo) is throwaway and may receive dirty
// builds.
tasks.withType<PublishToMavenRepository>().configureEach {
  if (!name.endsWith("ToPrivateRepository")) {
    dependsOn(validateVersion)
  }
}
