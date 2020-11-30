package io.sorne.tlang.loader.manifest

case class Dependency(organisation: String, project: String, name: String, version: String, stability: Stability.stability, releaseNumber: Int, alias: String) {

  def getModuleName: String = organisation + "/" + project + "/" + name

}
