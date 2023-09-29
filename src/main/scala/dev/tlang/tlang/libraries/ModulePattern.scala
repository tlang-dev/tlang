package dev.tlang.tlang.libraries

import dev.tlang.tlang.ast.helper.{HelperBlock, HelperFunc}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.ast.model.{ModelBlock, ModelContent}
import dev.tlang.tlang.ast.{DomainExpose, DomainHeader, DomainModel}
import dev.tlang.tlang.loader.manifest.{Dependency, Manifest}
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

abstract class ModulePattern {

  def getProject: String

  def getName: String

  def getFunctions: List[HelperFunc]

  def getModuleName: String = {
    Modules.organisation + "/" + getProject + "/" + getName
  }

  def getDependencies: Option[List[Dependency]] = None


  private def getMainResource: Resource = {
    Resource("", "", "", "Main", DomainModel(None, Some(DomainHeader(None, Some(exposeFunctions), None)), List(
      HelperBlock(None, Some(getFunctions)),
      ModelBlock(None, getModelContent)
    )))
  }

  private def getModelContent: Option[List[ModelContent[_]]] = {
    if (getModels.nonEmpty) Some(getModels)
    else None
  }

  def getModels: List[ModelContent[_]]

  private def exposeFunctions: List[DomainExpose] = {
    val exposes = ListBuffer.empty[DomainExpose]
    exposes ++= getFunctions.map(func => DomainExpose(None, func.name))
    if (getModelContent.isDefined) exposes ++= getModelContent.get.filter(_.isInstanceOf[ModelSetEntity]).map(entity => DomainExpose(None, entity.asInstanceOf[ModelSetEntity].name))
    exposes.toList
  }

  private def getResources: Map[String, Resource] = Map("Main" -> getMainResource)

  private def getExternalResources: Option[Map[String, Module]] = None

  private def getManifest: Manifest = {
    Manifest(getName, getProject, Modules.organisation, Modules.version, Some(Modules.stability), Modules.releaseNumber, getDependencies)
  }

  def getModule: Module = Module("", getManifest, getResources, getExternalResources, "Main")

}
