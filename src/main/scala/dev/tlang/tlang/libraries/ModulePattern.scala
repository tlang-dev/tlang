package dev.tlang.tlang.libraries

import dev.tlang.tlang.ast.helper.{HelperBlock, HelperFunc}
import dev.tlang.tlang.ast.{DomainExpose, DomainHeader, DomainModel}
import dev.tlang.tlang.loader.manifest.{Dependency, Manifest}
import dev.tlang.tlang.loader.{Module, Resource}

abstract class ModulePattern {

  def getProject: String

  def getName: String

  def getFunctions: List[HelperFunc]

  def getModuleName: String = {
    Modules.organisation + "/" + getProject + "/" + getName
  }

  def getDependencies: Option[List[Dependency]] = None


  def getMainResource: Resource = {
    Resource("", "", "", "Main", DomainModel(None, Some(DomainHeader(None, Some(exposeFunctions), None)), List(
      HelperBlock(None, Some(getFunctions))
    )))
  }

  def exposeFunctions: List[DomainExpose] = {
    getFunctions.map(func => DomainExpose(None, func.name))
  }

  def getResources: Map[String, Resource] = Map("Main" -> getMainResource)

  def getExternalResources: Option[Map[String, Module]] = None

  def getManifest: Manifest = {
    Manifest(getName, getProject, Modules.organisation, Modules.version, Some(Modules.stability), Modules.releaseNumber, getDependencies)
  }

  def getModule: Module = Module("", getManifest, getResources, getExternalResources, "Main")

}
