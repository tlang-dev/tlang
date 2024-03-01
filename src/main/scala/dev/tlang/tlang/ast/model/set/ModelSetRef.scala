package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, AstContext}
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class ModelSetRef(context: Option[ContextContent], refs: List[String], currying: Option[List[ModelSetRefCurrying]],
                       var func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]] = None, scope: Scope = Scope())
  extends ModelSetValueType[ModelSetRef] with ModelSetRefValue with HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
