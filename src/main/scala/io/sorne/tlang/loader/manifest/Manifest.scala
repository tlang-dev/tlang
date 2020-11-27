package io.sorne.tlang.loader.manifest

case class Manifest(
                     name: String,
                     project: String,
                     organisation: String,
                     version: String,
                     stability: Option[Stability.stability],
                     releaseNumber: Int,
                     dependencies: Option[List[Dependency]],
                   )
