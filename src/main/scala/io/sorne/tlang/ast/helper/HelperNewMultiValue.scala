package io.sorne.tlang.ast.helper

import io.sorne.tlang.ast.helper.call.HelperCallObject
import io.sorne.tlang.interpreter.Value

case class HelperNewMultiValue(values: List[Either[HelperCallObject, Value[_]]]) extends HelperStatement
