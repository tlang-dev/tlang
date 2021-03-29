package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplAttribute(context: Option[ContextContent], attr: Option[TmplID], `type`: Option[TmplType], value: TmplValueType) extends DeepCopy with AstContext {
  override def deepCopy(): TmplAttribute = TmplAttribute(context,
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy().asInstanceOf[TmplValueType]
  )

  override def getContext: Option[ContextContent] = context
}
