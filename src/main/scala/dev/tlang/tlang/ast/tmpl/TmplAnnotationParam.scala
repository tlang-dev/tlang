package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.primitive.TmplPrimitiveValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplAnnotationParam(context: Option[ContextContent], var name: Option[TmplID], var value: TmplValueType[_]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplAnnotationParam =
    TmplAnnotationParam(context,
      if(name.isDefined)Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
      value.deepCopy().asInstanceOf[TmplValueType[_]])

  override def getContext: Option[ContextContent] = context
}
