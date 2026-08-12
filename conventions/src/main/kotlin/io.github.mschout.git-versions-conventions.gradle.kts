plugins { id("pl.allegro.tech.build.axion-release") }

scmVersion {
  // Bare version tags ("0.6.0") rather than axion's default "v" prefix.
  tag { prefix.set("") }
}

// A clean, tagged commit resolves to "x.y.z"; anything else to "x.y.z-SNAPSHOT".
// Use `./gradlew release` to bump the version, tag, and push the tag.
version = scmVersion.version
