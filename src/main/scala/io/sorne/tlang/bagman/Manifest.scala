package io.sorne.tlang.bagman

import java.net.URL
import java.nio.file.Path

case class Manifest(path: Path, files: List[ManifestFile])

case class ManifestFile(name: String, fileType: String, checksum: String, remote: Option[URL] = None, local: Option[String] = None)
