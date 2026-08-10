plugins {
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.dokka")
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
