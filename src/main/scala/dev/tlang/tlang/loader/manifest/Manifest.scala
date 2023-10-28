package dev.tlang.tlang.loader.manifest

case class Manifest(
                     name: String,
                     project: String,
                     organisation: String,
                     version: String,
                     stability: Option[Stability.stability],
                     releaseNumber: Int,
                     main: Option[String],
                     dependencies: Option[List[Dependency]],
                   )
