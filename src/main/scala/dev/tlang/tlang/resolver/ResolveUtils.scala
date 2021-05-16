package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.loader.{Module, Resource}

object ResolveUtils {

  def findInFuncs(funcs: List[HelperFunc], name: String): Option[HelperFunc] = {
    var i = 0
    var func: Option[HelperFunc] = None
    while (func.isEmpty && i < funcs.size) {
      if (funcs(i).name == name) func = Some(funcs(i))
      i += 1
    }
    func
  }

  def findInVars(contents: List[ModelContent[_]], name: String): Option[AssignVar] = {
    var i = 0
    var entity: Option[AssignVar] = None
    while (entity.isEmpty && i < contents.size) {
      contents(i) match {
        case newEntity: AssignVar => if (newEntity.name == name) entity = Some(newEntity)
        case _ =>
      }
      i += 1
    }
    entity
  }

  def findInModels(contents: List[ModelContent[_]], name: String): Option[ModelSetEntity] = {
    var i = 0
    var entity: Option[ModelSetEntity] = None
    while (entity.isEmpty && i < contents.size) {
      contents(i) match {
        case newModel: ModelSetEntity => if (newModel.name == name) entity = Some(newModel)
        case _ =>
      }
      i += 1
    }
    entity
  }

  def findResource(use: DomainUse, module: Module): Option[Resource] = {
    val name = use.parts.mkString("/")
    module.resources.get(name) match {
      case Some(resource) => Some(resource)
      case None => module.extResources match {
        case Some(resources) => resources.get(use.parts.head) match {
          case Some(extModule) => extModule.resources.get(extModule.mainFile) match {
            case Some(resource) => Some(resource)
            case None => None
          }
          case None => None
        }
        case None => None
      }
    }
  }

}
