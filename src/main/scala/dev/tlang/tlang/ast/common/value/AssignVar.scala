package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.context.Scope

case class AssignVar(context: Option[ContextContent], name: String, `type`: Option[String] = None, value: ComplexValueStatement[_], scope: Option[Scope] = None) extends HelperStatement with ModelContent[AssignVar] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: AssignVar = this

  override def getType: String = "AssignVar"
}
