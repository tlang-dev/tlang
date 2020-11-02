package io.sorne.tlang.libraries.io

import java.io.{File, PrintWriter}

import io.sorne.tlang.ast.helper.{HelperCallFuncObject, HelperStatement}
import io.sorne.tlang.interpreter._

object File extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]] = {
    val arg1 = statement.asInstanceOf[HelperCallFuncObject]
    arg1.name.get match {
      case "write" =>
        val file = ExecCallObject.run(arg1.currying.get.head.attrs.head, context)
        val content = ExecCallObject.run(arg1.currying.get.head.attrs(1), context)
        write(file.toOption.get.asInstanceOf[`type`.String].getValue,
          content.toOption.get.asInstanceOf[`type`.String].getValue)
    }
  }

  def write(file: String, content: String): Either[ExecError, Option[Value[_]]] = {
    val pw = new PrintWriter(new File(file))
    pw.write(content)
    pw.close()
    Right(None)
  }
}
