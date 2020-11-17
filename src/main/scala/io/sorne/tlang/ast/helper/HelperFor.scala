package io.sorne.tlang.ast.helper

import io.sorne.tlang.ast.helper.call.HelperCallObject

case class HelperFor(variable: String, start: Option[HelperCallObject], forType: ForType.forType, array: HelperCallObject, body: HelperContent) extends HelperStatement
