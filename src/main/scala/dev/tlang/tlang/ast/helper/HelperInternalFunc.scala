package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.interpreter.{ExecError, Value}

case class HelperInternalFunc(func: Context => Either[ExecError, Option[List[Value[_]]]], scope: Scope = Scope()) extends HelperStatement
