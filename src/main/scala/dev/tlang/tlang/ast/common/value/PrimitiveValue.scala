package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.interpreter.ExecError

abstract class PrimitiveValue[TYPE] extends SimpleValueStatement[TYPE] {

  def add(value: TYPE): Either[ExecError, TYPE]

  def subtract(value: TYPE): Either[ExecError, TYPE]

  def multiply(value: TYPE): Either[ExecError, TYPE]

  def divide(value: TYPE): Either[ExecError, TYPE]

  def modulo(value: TYPE): Either[ExecError, TYPE]

}
