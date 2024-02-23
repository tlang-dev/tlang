package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class ModelSetRef(context: Null[ContextContent], refs: List[String], currying: Option[List[ModelSetRefCurrying]],
                       var func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]] = None, scope: Scope = Scope())
  extends ModelSetValueType[ModelSetRef] with ModelSetRefValue with HelperStatement with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
