package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import tlang.core.Value

case class HelperInternalFunc(func: Context => Either[ExecError, Option[List[Value]]], scope: Scope = Scope()) extends HelperStatement
