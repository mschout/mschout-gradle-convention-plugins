// This plugin ID becomes: io.github.mschout.all-conventions
// A single plugin that pulls in everything. Use this if you want the full suite.
// Or apply the individual plugins à la carte.

plugins {
  id("io.github.mschout.kotlin-conventions")
  id("io.github.mschout.spotless-conventions")
  id("io.github.mschout.jacoco-conventions")
  id("io.github.mschout.version-catalog-conventions")
  id("io.github.mschout.git-versions-conventions")
}
