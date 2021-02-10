package dev.tlang.tlang.ast.common.value

import io.sorne.tlang.ast.common.call.ComplexValueStatement

case class ComplexAttribute(attr: Option[String] = None, `type`: Option[String] = None, value: ComplexValueStatement[_])
