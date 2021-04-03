package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplSetAttribute(context: Option[ContextContent], var name: Option[TmplID], var value: TmplOperation) extends DeepCopy with AstContext {
  override def deepCopy(): TmplSetAttribute = TmplSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy())

  override def getContext: Option[ContextContent] = context
}
