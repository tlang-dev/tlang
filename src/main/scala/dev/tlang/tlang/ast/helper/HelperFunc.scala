package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope

case class HelperFunc(name: String, currying: Option[List[HelperCurrying]] = None,
                      returns: Option[List[HelperParamType]] = None, block: HelperContent, scope: Scope = Scope()) extends HelperStatement with Value[HelperFunc] {
  override def getValue: HelperFunc = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[HelperFunc]): Int = this.name.compareTo(value.getValue.name)
}
