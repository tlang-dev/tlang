package io.sorne.tlang.bagman

import java.net.URL

case class PkgManifest(path: String, files: List[PkgManifestFile]) {

  def toYaml: String = {
    val str = new StringBuilder
    str ++= "path: " ++= path ++= "\n"
    str ++= "files:\n"
    str ++= files.map(pkg => "  - " + pkg.toYaml.replaceAll("\n", "\n    ").replaceAll(": \n", ":\n")).mkString("\n")
    str.toString()
  }

}

case class PkgManifestFile(name: String, fileType: String, checksum: String, remote: Option[URL] = None, local: Option[String] = None) {

  def toYaml: String = {
    val str = new StringBuilder
    str ++= "name: " ++= name ++= "\n"
    str ++= "fileType: " ++= fileType ++= "\n"
    str ++= "checksum: " ++= checksum ++= "\n"
    str ++= "remote: " ++= remote.getOrElse("").toString ++= "\n"
    str ++= "local: " ++= local.getOrElse("")
    str.toString()
  }
}
