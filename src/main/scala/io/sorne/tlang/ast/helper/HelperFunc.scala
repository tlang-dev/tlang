package io.sorne.tlang.ast.helper

import io.sorne.tlang.interpreter.Value

case class HelperFunc(name: String, currying: Option[List[HelperCurrying]] = None, block: HelperContent) extends HelperStatement with Value[HelperFunc] {
  override def getValue: HelperFunc = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[HelperFunc]): Int = this.name.compareTo(value.getValue.name)
}
