package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.TmplBlock
import dev.tlang.tlang.tmpl.lang.ast.LangBlock

case class ModelSetRef(context: Option[ContextContent], refs: List[String], currying: Option[List[ModelSetRefCurrying]],
                       var func: Option[Either[HelperFunc, TmplBlock[_]]] = None, scope: Scope = Scope())
  extends ModelSetValueType[ModelSetRef] with ModelSetRefValue with HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetRef = this

  override def getType: String = "ModelSetRef"
}
