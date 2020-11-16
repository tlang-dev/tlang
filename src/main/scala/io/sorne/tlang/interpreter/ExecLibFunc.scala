package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.ast.helper.call.HelperCallFuncObject
import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.libraries.generator.Generator
import io.sorne.tlang.libraries.tio.IOFile

object ExecLibFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val arg1 = statement.asInstanceOf[HelperCallFuncObject]
    arg1.name.get match {
      case "File" => IOFile.run(arg1, context)
      case "Generator" => Generator.run(arg1, context)
      case _ => Left(CallableNotFound(arg1.name.getOrElse("No name")))
    }
  }
}
