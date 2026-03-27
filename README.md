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

### 1. Clone this repo alongside your project (or add as a Git submodule)

```
workspace/
├── mschout-convention-plugins/   ← this repo
└── my-app/                       ← your project
```

### 2. Include it in your project's `settings.gradle.kts`

```kotlin
pluginManagement {
    includeBuild("../mschout-convention-plugins")
}

rootProject.name = "my-app"
```

### 3. Apply the plugins in your `build.gradle.kts`

```kotlin
// Apply everything at once:
plugins {
    id("mschout.all-conventions")
}

// Or pick and choose:
plugins {
    id("mschout.kotlin-conventions")
    id("mschout.spotless-conventions")
}
```

That's it. No publishing, no repository hosting — Gradle builds the plugins
from source via the composite build.

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
- **Change the plugin ID prefix**: rename the `.gradle.kts` files (the filename
  minus `.gradle.kts` becomes the plugin ID).
- **Add more plugins**: create a new `mschout.foo-conventions.gradle.kts` in the same
  directory, add any required Gradle plugin dependencies to
  `conventions/build.gradle.kts`, and optionally wire it into
  `mschout.all-conventions.gradle.kts`.
- **Override in a consuming project**: anything set in the convention plugin can
  be overridden in the consuming project's `build.gradle.kts` — Gradle applies
  convention values first, then your project-level config wins.
