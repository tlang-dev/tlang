package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.call.SimpleValueStatement

case class HelperFor(variable: String, start: Option[SimpleValueStatement[_]], forType: ForType.forType, array: SimpleValueStatement[_], body: HelperContent) extends HelperStatement
