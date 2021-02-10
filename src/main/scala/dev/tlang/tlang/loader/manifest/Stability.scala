package dev.tlang.tlang.loader.manifest

object Stability extends Enumeration {
  type stability = Value
  val FINAL, RC, BETA, ALPHA, UNKNOWN = Value
}
