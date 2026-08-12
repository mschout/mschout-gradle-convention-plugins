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
│       ├── io.github.mschout.kotlin-conventions.gradle.kts
│       ├── io.github.mschout.spotless-conventions.gradle.kts
│       ├── io.github.mschout.jacoco-conventions.gradle.kts
│       ├── io.github.mschout.version-catalog-conventions.gradle.kts
│       ├── io.github.mschout.git-versions-conventions.gradle.kts
│       └── io.github.mschout.all-conventions.gradle.kts   # applies all of the above
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
    id("io.github.mschout.all-conventions") version "1.0.0"
}

// Or pick and choose:
plugins {
    id("io.github.mschout.kotlin-conventions") version "1.0.0"
    id("io.github.mschout.spotless-conventions") version "1.0.0"
}
```

Each plugin ID is resolved via its own published plugin-marker artifact, which
pulls in the base artifact `io.github.mschout:gradle-convention-plugins`. The IDs
are prefixed with `io.github.mschout` so that every marker artifact lives under
that (Maven Central–verified) namespace.

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
    id("io.github.mschout.all-conventions")
}
```

## Available plugins

| Plugin ID                  | What it does                                   |
| -------------------------- |------------------------------------------------|
| `io.github.mschout.kotlin-conventions`    | Kotlin JVM, toolchain 21, JUnit 5, common deps |
| `io.github.mschout.spotless-conventions`  | Spotless + ktfmt formatting                    |
| `io.github.mschout.jacoco-conventions`    | JaCoCo coverage reports             |
| `io.github.mschout.version-catalog-conventions` | Version catalog updates via [version-catalog-update](https://github.com/littlerobots/version-catalog-update-plugin) |
| `io.github.mschout.git-versions-conventions` | Git-based versioning via [Palantir git-version](https://github.com/palantir/gradle-git-version) |
| `io.github.mschout.all-conventions`       | Applies all of the above                       |

## Configuration via `gradle.properties`

The convention plugins read the following Gradle properties from the consuming
project's `gradle.properties` (or `-P` on the command line, or an
`ORG_GRADLE_PROJECT_*` environment variable):

| Property              | Default            | Used by              | What it does |
| --------------------- | ------------------ | -------------------- | ------------ |
| `jvmToolchainVersion` | `21`               | `kotlin-conventions` | The JDK version used to compile and run (the [JVM toolchain](https://docs.gradle.org/current/userguide/toolchains.html)). |
| `jvmTarget`           | `jvmToolchainVersion` | `kotlin-conventions` | The JVM bytecode target for both `javac` (via `--release`) and the Kotlin compiler. Set this lower than the toolchain to build with a newer JDK while producing bytecode (and API usage) compatible with an older JVM. |

For example, to build with a JDK 21 toolchain but produce JDK 17–compatible
bytecode:

```properties
jvmToolchainVersion=21
jvmTarget=17
```

Or on the command line:

```
./gradlew build -PjvmToolchainVersion=21 -PjvmTarget=17
```

No other properties are consumed by the plugins. (The Maven Central credential
properties described under [Releasing to Maven Central](#releasing-to-maven-central)
apply only when publishing this repo itself, and belong in
`~/.gradle/gradle.properties`, never in the project.)

## Customizing

- **Change the plugin ID prefix**: rename the `.gradle.kts` files (the filename
  minus `.gradle.kts` becomes the plugin ID). Note that a plugin's published
  marker artifact uses the plugin ID as its Maven group, so the ID must stay
  under the `io.github.mschout` namespace to be publishable to Maven Central.
- **Add more plugins**: create a new `io.github.mschout.foo-conventions.gradle.kts`
  in the same directory, add any required Gradle plugin dependencies to
  `conventions/build.gradle.kts`, and optionally wire it into
  `io.github.mschout.all-conventions.gradle.kts`.
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
