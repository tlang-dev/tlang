package dev.tlang.tlang.ast.helper

import io.sorne.tlang.ast.common.call.SimpleValueStatement

case class HelperFor(variable: String, start: Option[SimpleValueStatement[_]], forType: ForType.forType, array: SimpleValueStatement[_], body: HelperContent) extends HelperStatement
