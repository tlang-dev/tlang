package dev.tlang.tlang.bagman

import java.net.URL

case class PkgManifest(path: String, files: List[PkgManifestFile]) {

  val ln: String = System.lineSeparator()

  def toYaml: String = {
    val str = new StringBuilder
    str ++= "path: " ++= path ++= ln
    str ++= "files:" ++= ln
    str ++= files.map(pkg => "  - " + pkg.toYaml.replaceAll(ln, ln + "    ").replaceAll(": " + ln, ":" + ln)).mkString(ln)
    str.toString()
  }

}

case class PkgManifestFile(name: String, fileType: String, checksum: String, remote: Option[URL] = None, local: Option[String] = None) {

  val ln: String = System.lineSeparator()

  def toYaml: String = {
    val str = new StringBuilder
    str ++= "name: " ++= name ++= ln
    str ++= "fileType: " ++= fileType ++= ln
    str ++= "checksum: " ++= checksum ++= ln
    str ++= "remote: " ++= remote.getOrElse("").toString ++= ln
    str ++= "local: " ++= local.getOrElse("")
    str.toString()
  }
}
