package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.model.set.ModelSetValueType
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

abstract class PrimitiveValue[TYPE] extends ComplexValueStatement[TYPE] with ModelSetValueType[TYPE] with TmplNode[TYPE] {

  def add(value: PrimitiveValue[TYPE]): Either[ExecError, PrimitiveValue[TYPE]]

  def subtract(value: PrimitiveValue[TYPE]): Either[ExecError, PrimitiveValue[TYPE]]

  def multiply(value: PrimitiveValue[TYPE]): Either[ExecError, PrimitiveValue[TYPE]]

  def divide(value: PrimitiveValue[TYPE]): Either[ExecError, PrimitiveValue[TYPE]]

  def modulo(value: PrimitiveValue[TYPE]): Either[ExecError, PrimitiveValue[TYPE]]

}
