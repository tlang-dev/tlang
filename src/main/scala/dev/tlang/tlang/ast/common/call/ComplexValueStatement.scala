package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.ComplexValueType
import dev.tlang.tlang.ast.helper.HelperStatement

abstract class ComplexValueStatement[T] extends HelperStatement with ComplexValueType[T]
