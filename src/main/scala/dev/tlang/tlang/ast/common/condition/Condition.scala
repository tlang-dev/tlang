package dev.tlang.tlang.ast.common.condition

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class Condition(context: Option[ContextContent], statement1: SimpleValueStatement[_], condition: Option[ConditionType.condition] = None, statement2: Option[SimpleValueStatement[_]] = None,
                     link: Option[ConditionLink.condition] = None, nextBlock: Option[ConditionBlock] = None) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
