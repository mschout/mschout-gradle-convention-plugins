# mschout-convention-plugins

Reusable Gradle convention plugins for Kotlin projects. Include this repo as a
composite build in any project to get a consistent setup for:

- **Kotlin JVM** — toolchain, compiler options, JUnit 5
- **Spotless** — ktlint formatting for `.kt` and `.gradle.kts` files
- **JaCoCo** — code coverage with XML + HTML reports
- **Version Catalog Update** — automated dependency updates for `libs.versions.toml`
- **Git Versions** — derive project version from Git tags

## Repository structure

```
mschout-convention-plugins/
├── settings.gradle.kts
├── conventions/
│   ├── build.gradle.kts          # declares plugin dependencies
│   └── src/main/kotlin/
│       ├── mschout.kotlin-conventions.gradle.kts
│       ├── mschout.spotless-conventions.gradle.kts
│       ├── mschout.jacoco-conventions.gradle.kts
│       ├── mschout.version-catalog-conventions.gradle.kts
│       ├── mschout.git-versions-conventions.gradle.kts
│       └── mschout.all-conventions.gradle.kts   # applies all of the above
```

## Usage in a consuming project

There are two ways to consume these plugins: from **Maven Central** (a published
release) or as a **composite build** (built from source, no publishing).

### Option A — From Maven Central (published release)

The plugins are published under the group `io.github.mschout`. Make Maven Central
available for plugin resolution in your project's `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "my-app"
```

Then apply the plugins in your `build.gradle.kts`, specifying a version:

```kotlin
// Apply everything at once:
plugins {
    id("mschout.all-conventions") version "1.0.0"
}

// Or pick and choose:
plugins {
    id("mschout.kotlin-conventions") version "1.0.0"
    id("mschout.spotless-conventions") version "1.0.0"
}
```

Each plugin ID is resolved via its own published plugin-marker artifact, which
pulls in the base artifact `io.github.mschout:gradle-convention-plugins`.

### Option B — Composite build (no publishing)

Clone this repo alongside your project (or add it as a Git submodule):

```
workspace/
├── mschout-convention-plugins/   ← this repo
└── my-app/                       ← your project
```

Include it in your project's `settings.gradle.kts`:

```kotlin
pluginManagement {
    includeBuild("../mschout-convention-plugins")
}

rootProject.name = "my-app"
```

Apply the plugins in your `build.gradle.kts` (no version needed — Gradle builds
them from source):

```kotlin
plugins {
    id("mschout.all-conventions")
}
```

## Available plugins

| Plugin ID                  | What it does                                   |
| -------------------------- |------------------------------------------------|
| `mschout.kotlin-conventions`    | Kotlin JVM, toolchain 21, JUnit 5, common deps |
| `mschout.spotless-conventions`  | Spotless + ktfmt formatting                    |
| `mschout.jacoco-conventions`    | JaCoCo coverage reports             |
| `mschout.version-catalog-conventions` | Version catalog updates via [version-catalog-update](https://github.com/littlerobots/version-catalog-update-plugin) |
| `mschout.git-versions-conventions` | Git-based versioning via [Palantir git-version](https://github.com/palantir/gradle-git-version) |
| `mschout.all-conventions`       | Applies all of the above                       |

## Customizing

- **JVM toolchain version**: defaults to 21. Override by setting the
  `jvmToolchainVersion` Gradle property in your project's `gradle.properties`:
  ```properties
  jvmToolchainVersion=17
  ```
  or on the command line:
  ```
  ./gradlew build -PjvmToolchainVersion=17
  ```
- **JVM target version**: defaults to the toolchain version. Override by setting
  the `jvmTarget` Gradle property to compile bytecode for a different JVM target
  than the toolchain. For example, to use a JDK 21 toolchain but produce
  JDK 17–compatible bytecode:
  ```properties
  jvmToolchainVersion=21
  jvmTarget=17
  ```
- **Change the plugin ID prefix**: rename the `.gradle.kts` files (the filename
  minus `.gradle.kts` becomes the plugin ID).
- **Add more plugins**: create a new `mschout.foo-conventions.gradle.kts` in the same
  directory, add any required Gradle plugin dependencies to
  `conventions/build.gradle.kts`, and optionally wire it into
  `mschout.all-conventions.gradle.kts`.
- **Override in a consuming project**: anything set in the convention plugin can
  be overridden in the consuming project's `build.gradle.kts` — Gradle applies
  convention values first, then your project-level config wins.

## Releasing to Maven Central

Publishing is handled by the
[gradle-maven-publish-plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/),
configured in `conventions/build.gradle.kts`. Artifacts are published under
`io.github.mschout` to the [Central Portal](https://central.sonatype.com/).

### Version

The published version is derived from Git tags via
[Palantir git-version](https://github.com/palantir/gradle-git-version). Tag a
clean commit to set the release version:

```bash
git tag 1.0.0
git push origin 1.0.0
```

A build with uncommitted changes produces a `…dirty` version; the
`validateVersion` task refuses to publish it to Maven Central.

### Credentials

The plugin reads these from `~/.gradle/gradle.properties` (or the equivalent
`ORG_GRADLE_PROJECT_*` environment variables) — **never commit them**:

```properties
# Central Portal user token (https://central.sonatype.com/account)
mavenCentralUsername=...
mavenCentralPassword=...

# GPG signing key, ASCII-armored (exported with `gpg --export-secret-keys --armor`)
signingInMemoryKey=-----BEGIN PGP PRIVATE KEY BLOCK-----\n...
signingInMemoryKeyId=12345678
signingInMemoryKeyPassword=...
```

### Publish

```bash
# Stage and automatically release to Maven Central (automaticRelease = true)
./gradlew :conventions:publishToMavenCentral

# Test the artifacts locally first (installs to ~/.m2)
./gradlew :conventions:publishToMavenLocal
```

This publishes the base artifact `io.github.mschout:gradle-convention-plugins`
plus a plugin-marker artifact for every `mschout.*` plugin, so consumers can
resolve them by ID with a `version`.
