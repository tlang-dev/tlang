package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplType(context: Option[ContextContent], var name: TmplID, var generic: Option[TmplGeneric] = None, isArray: Boolean = false) extends DeepCopy with AstContext {
  override def deepCopy(): TmplType = TmplType(context, name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false)

  override def getContext: Option[ContextContent] = context
}
