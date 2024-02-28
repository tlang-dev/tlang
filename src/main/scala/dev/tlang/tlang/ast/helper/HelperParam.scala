package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.ValueType
import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class HelperParam(context: Null, param: Option[String], `type`: ValueType) extends Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
