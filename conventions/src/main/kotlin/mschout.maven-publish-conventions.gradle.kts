plugins {
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.dokka")
}

val validateVersion by tasks.registering {
  doLast {
    val version = project.version.toString()
    if (version.endsWith("dirty")) {
      throw GradleException("Cannot publish a dirty version: $version")
    }
  }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
  dependsOn(validateVersion)
}
