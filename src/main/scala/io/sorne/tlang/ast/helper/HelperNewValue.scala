package io.sorne.tlang.ast.helper

import io.sorne.tlang.interpreter.Value

case class HelperNewValue(value: Value[_]) extends HelperStatement
