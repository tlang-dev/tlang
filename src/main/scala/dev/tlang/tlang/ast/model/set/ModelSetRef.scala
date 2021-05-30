package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.tmpl.TmplBlock
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetRef(context: Option[ContextContent], refs: List[String], currying: Option[List[ModelSetRefCurrying]], var func: Option[Either[HelperFunc, TmplBlock]] = None)
  extends ModelSetValueType[ModelSetRef] with ModelSetRefValue with HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetRef = this

  override def getType: String = "ModelSetRef"
}
