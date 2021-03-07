package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

class TLangDouble(context: Option[ContextContent], value: Double) extends PrimitiveValue[Double] {
  override def getElement: Double = value

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getElement)

  override def toString: String = getElement.toString

  override def getContext: Option[ContextContent] = context
}

object TLangDouble extends TLangType {
  override def getType: String = "Double"
}
