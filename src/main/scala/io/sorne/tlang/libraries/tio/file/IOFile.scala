package io.sorne.tlang.libraries.tio.file

import java.io.{File, PrintWriter}

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.ast.helper.call.HelperCallFuncObject
import io.sorne.tlang.interpreter.`type`.TLangString
import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.interpreter.{ExecCallObject, ExecError, Executor, Value}

object IOFile extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val arg1 = statement.asInstanceOf[HelperCallFuncObject]
    arg1.name.get match {
      case "write" =>
        val file = ExecCallObject.run(arg1.currying.get.head.attrs.head, context)
        val content = ExecCallObject.run(arg1.currying.get.head.attrs(1), context)
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
