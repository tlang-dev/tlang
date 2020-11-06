package io.sorne.tlang.loader

import scala.collection.immutable

case class Module(rootDir: String, resources: immutable.Map[String, Resource], extResources: Option[Map[String, Module]], mainFile: String)
