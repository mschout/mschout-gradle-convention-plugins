// This plugin ID becomes: mschout.jacoco-conventions
// Apply it in projects with: id("mschout.jacoco-conventions")

plugins {
  java
  jacoco
}

jacoco { toolVersion = "0.8.12" }

tasks.withType<Test>() { finalizedBy("jacocoTestReport") }

tasks.jacocoTestReport {
  dependsOn(tasks.test)

  reports {
    xml.required.set(true) // useful for CI tools (Codecov, SonarQube, etc.)
    html.required.set(true) // nice for local browsing
    csv.required.set(false)
  }
}

// Optional: enforce minimum coverage. Uncomment and tune as needed.
// tasks.jacocoTestCoverageVerification {
//     violationRules {
//         rule {
//             limit {
//                 minimum = BigDecimal("0.80")
//             }
//         }
//     }
// }

// Wire up: running `check` also produces the coverage report
tasks.named("check") { dependsOn(tasks.jacocoTestReport) }
