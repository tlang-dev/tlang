package dev.tlang.tlang.loader.manifest

case class Dependency(organisation: String, project: String, name: String, version: String, stability: Stability.stability, releaseNumber: Int, alias: Option[String] = None, dir: Option[String] = None) {

  def getModuleName: String = organisation + "/" + project + "/" + name

}
