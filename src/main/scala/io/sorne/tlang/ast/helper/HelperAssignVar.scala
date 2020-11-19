package io.sorne.tlang.ast.helper

import io.sorne.tlang.interpreter.Value

case class HelperAssignVar(name: String, value: Either[HelperStatement, Value[_]]) extends HelperStatement
