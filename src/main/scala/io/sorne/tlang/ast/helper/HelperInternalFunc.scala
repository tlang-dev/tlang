package io.sorne.tlang.ast.helper

import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.interpreter.{ExecError, Value}

case class HelperInternalFunc(func: Context => Either[ExecError, Option[List[Value[_]]]]) extends HelperStatement
