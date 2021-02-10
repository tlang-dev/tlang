package dev.tlang.tlang.ast.common.call

import io.sorne.tlang.ast.common.value.SimpleValueType

abstract class SimpleValueStatement[T] extends ComplexValueStatement[T] with SimpleValueType[T]
