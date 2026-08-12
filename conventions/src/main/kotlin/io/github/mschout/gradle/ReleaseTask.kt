package io.github.mschout.gradle

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask
import org.gradle.process.ExecOperations

/**
 * Cuts a release: tags the current commit, then pushes the branch and tags. Publish afterwards in a
 * separate Gradle invocation (git-version resolves the project version at configuration time, so
 * the new tag must exist before the publishing build is configured):
 * `./gradlew publishAndReleaseToMavenCentral`
 *
 * Usage: `./gradlew release -PreleaseVersion=x.y.z`
 */
@UntrackedTask(because = "Releasing tags and pushes; it must always run")
abstract class ReleaseTask @Inject constructor(private val execOps: ExecOperations) :
    DefaultTask() {

  /** The version to release, e.g. "0.6.0". The tag is created without a "v" prefix. */
  @get:Input @get:Optional abstract val releaseVersion: Property<String>

  /** The directory containing the git repository. */
  @get:Internal abstract val rootDir: DirectoryProperty

  @TaskAction
  fun release() {
    val version =
        releaseVersion.orNull
            ?: throw GradleException(
                "No release version given. Usage: ./gradlew release -PreleaseVersion=x.y.z"
            )

    val workDir = rootDir.get().asFile

    val status = ByteArrayOutputStream()
    execOps.exec {
      workingDir = workDir
      commandLine("git", "status", "--porcelain")
      standardOutput = status
    }
    if (status.toString().isNotBlank()) {
      throw GradleException("Working tree is dirty; commit or stash before releasing.")
    }

    fun git(vararg args: String) {
      execOps.exec {
        workingDir = workDir
        commandLine("git", *args)
      }
    }

    git("tag", "-a", version, "-m", "v$version")
    git("push", "origin")
    git("push", "origin", "--tags")
  }
}
