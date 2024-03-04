package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.helper.HelperBlock
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.AstAnyTmplBlock

object ReferenceFinder {

  /*def addUses(context: BuilderContext): Unit = {
    context.resource.ast.header.foreach(_.uses.foreach(_.foreach(use => {
      val name = if (use.alias.isDefined) use.alias.get else use.parts.last
      context.module.resources.get(use.parts.head) match {
        case Some(resource) => context.callables += (name -> BuildProgram.getContentType(context.resource.ast.context, None) + "/" + use.parts.mkString("/"))
        case None => findInResources(context, use.parts, name)
      }
    })))
  }

  def findInResources(context: BuilderContext, parts: List[String], name: String): Unit = {
    val fullName = BuildProgram.getContentType(context.resource.ast.context, None) + "/" + parts.mkString("/")
    if (context.module.resources.contains(fullName)) context.callables += (name -> fullName)
    else context.module.extResources.foreach(_.get(parts.head).foreach(module => {
      val fullName = BuildProgram.getContentType(context.resource.ast.context, None) + "/" + parts.last
      context.callables += (name -> fullName)
    }))
  }

  def addCallables(context: BuilderContext): Unit = {
    context.resource.ast.body.foreach {
      case helper: HelperBlock => addInHelper(context, helper)
      case model: ModelBlock => addInModel(context, model)
      case tmpl: AstAnyTmplBlock => addTmpls(context, tmpl)
    }
  }

  private def addInHelper(context: BuilderContext, helper: HelperBlock): Unit = {
    helper.funcs.foreach(_.foreach(func => {
      context.callables += (func.name -> BuildProgram.getContentType(func.context, Some(func.name)))
    }))
  }

  private def addInModel(context: BuilderContext, model: ModelBlock): Unit = {
    model.content.foreach(_.foreach {
      case assign: AssignVar => context.callables += (assign.name -> BuildProgram.getContentType(assign.context, Some(assign.name)))
      case setEntity: ModelSetEntity => context.callables += (setEntity.name.getSimpleType.toString -> BuildProgram.getContentType(setEntity.context, Some(setEntity.name.getSimpleType.toString)))
    })
  }

  private def addTmpls(context: BuilderContext, tmpl: AstAnyTmplBlock): Unit = {
   context.callables += (tmpl.getName -> BuildProgram.getContentType(tmpl.getContext, Some(tmpl.getName)))
  }*/

}
