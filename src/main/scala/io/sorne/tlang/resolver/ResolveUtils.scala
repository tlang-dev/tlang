package io.sorne.tlang.resolver

import io.sorne.tlang.ast.DomainUse
import io.sorne.tlang.ast.common.value.AssignVar
import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.ast.model.ModelContent
import io.sorne.tlang.loader.{Module, Resource}

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

  def findInVars(contents: List[ModelContent], name: String): Option[AssignVar] = {
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
