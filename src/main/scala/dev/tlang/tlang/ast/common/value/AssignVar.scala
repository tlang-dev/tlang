package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.context.Scope

case class AssignVar(context: Option[ContextContent], name: String, var `type`: Option[ValueType] = None, value: Operation, scope: Scope = Scope()) extends HelperStatement with ModelContent[AssignVar] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: AssignVar = this

  override def getType: String = "AssignVar"
}
