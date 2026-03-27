import com.diffplug.spotless.kotlin.KtfmtStep

// This plugin ID becomes: mschout.spotless-conventions
// Apply it in projects with: id("mschout.spotless-conventions")

plugins { com.diffplug.spotless }

spotless {
  kotlin {
    ktfmt("0.62").configure {
      it.setBlockIndent(2)
      it.setContinuationIndent(4)
      it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.COMPLETE)
      it.setRemoveUnusedImports(true)
    }
    trimTrailingWhitespace()
    endWithNewline()

    // apply license header file if it exists
    if (rootProject.file("LICENSE_HEADER").exists()) {
      licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}
