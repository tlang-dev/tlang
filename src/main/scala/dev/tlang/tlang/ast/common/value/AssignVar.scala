package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.interpreter.context.Scope
import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class AssignVar(context: Null, name: String, `type`: Option[ValueType] = None, value: Operation, scope: Scope = Scope()) extends HelperStatement with ModelContent[AssignVar] with Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
