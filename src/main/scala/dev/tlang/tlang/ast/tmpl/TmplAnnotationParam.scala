package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.primitive.TmplPrimitiveValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplAnnotationParam(context: Option[ContextContent], var name: TmplID, var value: TmplValueType[_]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplAnnotationParam =
    TmplAnnotationParam(context, name.deepCopy().asInstanceOf[TmplID], value.deepCopy().asInstanceOf[TmplPrimitiveValue[_]])

  override def getContext: Option[ContextContent] = context
}
