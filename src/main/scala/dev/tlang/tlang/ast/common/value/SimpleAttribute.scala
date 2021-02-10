package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.SimpleValueStatement

case class SimpleAttribute(attr: Option[String] = None, `type`: Option[String] = None, value: SimpleValueStatement[_])
