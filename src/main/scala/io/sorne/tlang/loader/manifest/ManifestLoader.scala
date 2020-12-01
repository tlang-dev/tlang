package io.sorne.tlang.loader.manifest

import java.util

import org.snakeyaml.engine.v2.api.{Load, LoadSettings}

import scala.jdk.CollectionConverters._

object ManifestLoader {

  def parseManifest(content: String): Manifest = {
    val settings = LoadSettings.builder().build()
    val load = new Load(settings)
    val map = load.loadFromString(content).asInstanceOf[util.Map[String, Object]].asScala.toMap
    mapToManifest(map)
  }

  def mapToManifest(elems: Map[String, _]): Manifest = {
    Manifest(
      elems.getOrElse("name", "").asInstanceOf[String],
      elems.getOrElse("project", "").asInstanceOf[String],
      elems.getOrElse("organisation", "").asInstanceOf[String],
      elems.getOrElse("version", "").asInstanceOf[String],
      extractStability(elems),
      elems.getOrElse("releaseNumber", 1).asInstanceOf[Int],
      getDependencies(elems),
    )
  }

  def extractStability(elems: Map[String, _]): Option[Stability.stability] = {
    if (elems.contains("stability")) getStability(elems("stability").asInstanceOf[String]) else None
  }

  def getStability(stability: String): Option[Stability.stability] = {
    stability match {
      case "final" => Some(Stability.FINAL)
      case "rc" => Some(Stability.RC)
      case "beta" => Some(Stability.BETA)
      case "alpha" => Some(Stability.ALPHA)
      case _ => None
    }
  }

  def getDependencies(elems: Map[String, _]): Option[List[Dependency]] = {
    if (elems.contains("dependencies")) {
      val deps = elems("dependencies").asInstanceOf[util.ArrayList[String]].asScala.toList
      Some(deps.map(dep => getDependency(dep)))
    } else None
  }

  def getDependency(dependency: String): Dependency = {
    val parts = dependency.split(" ")
    val names = parts(0).split("/")
    val versions = parts(1).split(":")
    val alias = parts(2)
    Dependency(
      names(0),
      names(1),
      names(2),
      versions(0),
      getStability(versions(1)).getOrElse(Stability.FINAL),
      versions(2).toInt,
      alias
    )
  }

}
