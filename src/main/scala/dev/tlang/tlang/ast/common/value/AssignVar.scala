package dev.tlang.tlang.ast.common.value

import io.sorne.tlang.ast.common.call.ComplexValueStatement
import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.ast.model.ModelContent

case class AssignVar(name: String, `type`: Option[String] = None, value: ComplexValueStatement[_]) extends HelperStatement with ModelContent
