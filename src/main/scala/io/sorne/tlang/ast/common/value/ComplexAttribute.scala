package io.sorne.tlang.ast.common.value

import io.sorne.tlang.ast.common.call.ComplexValueStatement
import io.sorne.tlang.ast.model.set.ModelSetRefValue

case class ComplexAttribute(attr: Option[String] = None, value: ComplexValueStatement[_])
