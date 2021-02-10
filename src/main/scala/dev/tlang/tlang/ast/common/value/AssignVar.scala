package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.ModelContent

case class AssignVar(name: String, `type`: Option[String] = None, value: ComplexValueStatement[_]) extends HelperStatement with ModelContent
