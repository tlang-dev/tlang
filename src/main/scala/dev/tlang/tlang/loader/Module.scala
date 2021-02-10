package dev.tlang.tlang.loader

import dev.tlang.tlang.loader.manifest.Manifest
import io.sorne.tlang.loader.manifest.Manifest

import scala.collection.immutable

case class Module(rootDir: String, manifest: Manifest, resources: immutable.Map[String, Resource], extResources: Option[Map[String, Module]], mainFile: String)
