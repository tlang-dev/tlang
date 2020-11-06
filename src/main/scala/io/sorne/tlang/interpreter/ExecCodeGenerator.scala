package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.ast.tmpl.TmplImpl
import io.sorne.tlang.generator.scala.ScalaGenerator
import io.sorne.tlang.interpreter.`type`.TLangString
import io.sorne.tlang.interpreter.context.Context

object ExecCodeGenerator extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val tmpl = TmplImpl("", None)
    val code = new ScalaGenerator().genClasses(tmpl)
    Right(Some(List(new TLangString(code))))
  }
}
