package dev.tlang.tlang.ast.common.call

import io.sorne.tlang.ast.common.value.ComplexValueType
import io.sorne.tlang.ast.helper.HelperStatement

abstract class ComplexValueStatement[T] extends HelperStatement with ComplexValueType[T]
