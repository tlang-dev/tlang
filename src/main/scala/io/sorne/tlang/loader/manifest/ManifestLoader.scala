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
      getStability(elems),
      elems.getOrElse("releaseNumber", 1).asInstanceOf[Int],
      getDependencies(elems),
    )
  }

  def getStability(elems: Map[String, _]): Option[Stability.stability] = {
    if (elems.contains("stability")) elems("stability") match {
      case "final" => Some(Stability.FINAL)
      case "rc" => Some(Stability.RC)
      case "beta" => Some(Stability.BETA)
      case "alpha" => Some(Stability.ALPHA)
      case _ => None
    } else None
  }

  def getDependencies(elems: Map[String, _]): Option[List[Dependency]] = {
    if (elems.contains("dependencies")) {
      val deps = elems("dependencies").asInstanceOf[util.ArrayList[util.Map[String, Object]]].asScala.toList
      Some(deps.map(dep => getDependency(dep.asScala.toMap)))
    } else None
  }

  def getDependency(elem: Map[String, _]): Dependency = {
    Dependency(
      elem.getOrElse("organisation", "").asInstanceOf[String],
      elem.getOrElse("project", "").asInstanceOf[String],
      elem.getOrElse("name", "").asInstanceOf[String],
      elem.getOrElse("version", "").asInstanceOf[String],
      getStability(elem).getOrElse(Stability.FINAL),
      elem.getOrElse("releaseNumber", 1).asInstanceOf[Int],
      elem.getOrElse("alias", "").asInstanceOf[String],
    )
  }

}
