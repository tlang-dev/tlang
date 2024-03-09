package dev.tlang.tlang.resolver

import dev.tlang.tlang.interpreter.value.InterValue
import dev.tlang.tlang.loader.Resource

case class PathContext(resource: Resource, links: Map[String, InterValue], pkg: String, resourceName: String, relatedPath: String) {

  def getResourcePath: String = pkg + "/" + resourceName

  def getFullPath: String = getResourcePath + "/" + relatedPath
}
