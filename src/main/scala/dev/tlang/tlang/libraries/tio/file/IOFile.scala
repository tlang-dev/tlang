package dev.tlang.tlang.libraries.tio.file

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import tlang.core.{Null, Value}

import java.io.{File, PrintWriter}
import java.nio.file.Files

object IOFile {

  def writeFunc: HelperFunc = HelperFunc(Null.empty(), "write", Some(List(HelperCurrying(Null.empty(), List(HelperParam(Null.empty(), Some("file"), ObjType(Null.empty(), None, TLangString.getType)), HelperParam(Null.empty(), Some("content"), ObjType(Null.empty(), None, TLangString.getType)))))), Null.empty(), HelperContent(Null.empty(), Some(List(
    HelperInternalFunc((context: Context) => {
      ContextUtils.findVar(context, "file") match {
        case Some(file) => ContextUtils.findVar(context, "content") match {
          case Some(content) => write(file.asInstanceOf[TLangString].getElement, content.asInstanceOf[TLangString].getElement)
          case None => println("Variable 'content' not found in context")
        }
        case None => println("Variable 'file' not found in context")
      }
      Right(None)
    })
  ))))

  /* override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
     val arg1 = statement.asInstanceOf[CallFuncObject]
     arg1.name.get match {
       case "write" =>
         val file = ExecComplexValue.run(arg1.currying.get.head.params.get.head.value, context)
         val content = ExecComplexValue.run(arg1.currying.get.head.params.get.head.value, context)
         write(file.toOption.get.asInstanceOf[TLangString].getValue,
           content.toOption.get.asInstanceOf[TLangString].getValue)
     }
   }*/

  def write(file: String, content: String): Either[ExecError, Option[List[Value[_]]]] = {
    val sysFile = new File(file)
    val parent = sysFile.getParentFile.toPath
    Files.createDirectories(parent)
    val pw = new PrintWriter(sysFile)
    pw.write(content)
    pw.close()
    Right(None)
  }
}
