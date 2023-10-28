package dev.tlang.tlang.loader.manifest

import org.snakeyaml.engine.v2.api.{Load, LoadSettings}

import java.util
import scala.jdk.CollectionConverters._

object ManifestLoader {

  def parseManifest(content: String): Manifest = {
    val map = loadFromString(content)
    mapToManifest(map)
  }

  def loadFromString(content: String): Map[String, Object] = {
    val settings = LoadSettings.builder().build()
    val load = new Load(settings)
    load.loadFromString(content).asInstanceOf[util.Map[String, Object]].asScala.toMap
  }

  def mapToManifest(elems: Map[String, _]): Manifest = {
    Manifest(
      elems.getOrElse("name", "").asInstanceOf[String],
      elems.getOrElse("project", "").asInstanceOf[String],
      elems.getOrElse("organisation", "").asInstanceOf[String],
      elems.getOrElse("version", "").asInstanceOf[String],
      extractStability(elems),
      elems.getOrElse("releaseNumber", 1).asInstanceOf[Int],
      getMain(elems),
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

  def getMain(elems: Map[String, _]): Option[String] = {
    if (elems.contains("main")) Some(elems("main").asInstanceOf[String]) else None
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
    val dir =
      if (dependency.contains("[")) Some(dependency.substring(dependency.indexOf("[") + 1, dependency.indexOf("]")))
      else None
    Dependency(
      names(0),
      names(1),
      names(2),
      versions(0),
      getStability(versions(1)).getOrElse(Stability.FINAL),
      versions(2).toInt,
      Some(alias),
      dir
    )
  }

}
