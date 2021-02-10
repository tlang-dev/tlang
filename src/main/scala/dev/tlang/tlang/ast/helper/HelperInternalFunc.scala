package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.interpreter.Value
import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.interpreter.{ExecError, Value}

case class HelperInternalFunc(func: Context => Either[ExecError, Option[List[Value[_]]]]) extends HelperStatement
