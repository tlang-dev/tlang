package io.sorne.tlang.ast.common.call

case class SetAttribute(name: Option[String] = None, value: ComplexValueStatement[_])
