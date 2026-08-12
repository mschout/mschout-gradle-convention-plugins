plugins {
  // Git-based versioning (axion-release) is part of the publishing conventions:
  // published versions always derive from git tags, and axion's `release` task
  // handles tagging and pushing.
  id("io.github.mschout.git-versions-conventions")
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.dokka")
}

// axion-release yields "x.y.z" only when HEAD sits on a clean, tagged commit;
// anything else resolves to "x.y.z-SNAPSHOT".
val validateVersion =
    tasks.register("validateVersion") {
      doLast {
        val version = project.version.toString()
        if (version.endsWith("-SNAPSHOT")) {
          throw GradleException("Cannot publish a snapshot version: $version")
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
