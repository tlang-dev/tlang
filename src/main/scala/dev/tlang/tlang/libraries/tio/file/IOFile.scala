package dev.tlang.tlang.libraries.tio.file

import java.io.{File, PrintWriter}

import dev.tlang.tlang.ast.common.call.CallFuncObject
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.interpreter.{ExecComplexValue, ExecError, Executor, Value}

object IOFile extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val arg1 = statement.asInstanceOf[CallFuncObject]
    arg1.name.get match {
      case "write" =>
        val file = ExecComplexValue.run(arg1.currying.get.head.params.get.head.value, context)
        val content = ExecComplexValue.run(arg1.currying.get.head.params.get.head.value, context)
        write(file.toOption.get.asInstanceOf[TLangString].getValue,
          content.toOption.get.asInstanceOf[TLangString].getValue)
    }
  }

  def write(file: String, content: String): Either[ExecError, Option[List[Value[_]]]] = {
    val pw = new PrintWriter(new File(file))
    pw.write(content)
    pw.close()
    Right(None)
  }
}
