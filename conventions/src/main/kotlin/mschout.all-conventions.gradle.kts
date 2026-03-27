// This plugin ID becomes: mschout.all-conventions
// A single plugin that pulls in everything. Use this if you want the full suite.
// Or apply the individual plugins à la carte.

plugins {
  id("mschout.kotlin-conventions")
  id("mschout.spotless-conventions")
  id("mschout.jacoco-conventions")
  id("mschout.version-catalog-conventions")
}
