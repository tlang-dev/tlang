package dev.tlang.tlang.libraries

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.ast.model.{ModelBlock, ModelContent}
import dev.tlang.tlang.ast.{DomainExpose, DomainHeader, DomainModel}
import dev.tlang.tlang.loader.manifest.{Dependency, Manifest}
import dev.tlang.tlang.loader.{Module, Resource}
import dev.tlang.tlang.tmpl.AstModel

import scala.collection.mutable.ListBuffer

abstract class ModulePattern {

  def getProject: String

  def getName: String

  def getFunctions: List[HelperFunc]

  def getMain: String = "Main"

  def getModuleName: String = {
    Modules.organisation + "/" + getProject + "/" + getName
  }

  def getDependencies: Option[List[Dependency]] = None


  protected def getMainResource: Resource = {
    Resource("", "", "", getMain, DomainModel(None, Some(DomainHeader(None, Some(exposeFunctions), None)), List(
      //      HelperBlock(Null.empty(), Null.of(getFunctions)),
//      ModelBlock(None, getModelContent)
    )))
  }

  private def getModelContent: Option[List[AstModel]] = {
    if (getModels.nonEmpty) Some(getModels)
    else None
  }

  def getModels: List[AstModel]

  private def exposeFunctions: List[DomainExpose] = {
    val exposes = ListBuffer.empty[DomainExpose]
    exposes ++= getFunctions.map(func => DomainExpose(None, func.getElement.asInstanceOf[HelperFunc].name))
    if (getModelContent.isDefined) exposes ++= getModelContent.get.filter(_.isInstanceOf[AstModel]).map(entity => DomainExpose(None, entity.asInstanceOf[ModelSetEntity].name.getSimpleType.toString)
    )
    exposes.toList
  }

  protected def getResources: Map[String, Resource] = Map(getMain -> getMainResource)

  private def getExternalResources: Option[Map[String, Module]] = None

  private def getManifest: Manifest = {
    Manifest(getName, getProject, Modules.organisation, Modules.version, Some(Modules.stability), Modules.releaseNumber, None, getDependencies)
  }

  def getModule: Module = Module("", getManifest, getResources, getExternalResources, getMain, isInternal = true)

}
