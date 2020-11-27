package io.sorne.tlang.libraries

import io.sorne.tlang.ast.DomainModel
import io.sorne.tlang.ast.helper.{HelperBlock, HelperFunc}
import io.sorne.tlang.loader.manifest.{Dependency, Manifest}
import io.sorne.tlang.loader.{Module, Resource}

abstract class ModulePattern {

  def getProject: String

  def getName: String

  def getFunctions: List[HelperFunc]

  def getModuleName: String = {
    Modules.organisation + "/" + getProject + "/" + getName
  }

  def getDependencies: Option[List[Dependency]] = None


  def getMainResource: Resource = {
    Resource("", "", "", "Main", DomainModel(None, List(
      HelperBlock(Some(getFunctions))
    )))
  }

  def getResources: Map[String, Resource] = Map("Main" -> getMainResource)

  def getExternalResources: Option[Map[String, Module]] = None

  def getManifest: Manifest = {
    Manifest(Modules.organisation, getProject, getName, Modules.version, Some(Modules.stability), Modules.releaseNumber, getDependencies)
  }

  def getModule: Module = Module("", getManifest, getResources, getExternalResources, "Main")

}
