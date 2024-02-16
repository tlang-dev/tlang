package dev.tlang.tlang.libraries.tio.terminal

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import tlang.core.Null

object Terminal {

  def printlnFunc: HelperFunc = HelperFunc(Null.empty(), "println", Some(List(HelperCurrying(Null.empty(), List(HelperParam(Null.empty(), Some("str"), ObjType(Null.empty(), None, TLangString.getType)))))), Null.empty(), HelperContent(Null.empty(), Some(List(
    HelperInternalFunc((context: Context) => {
      ContextUtils.findVar(context, "str") match {
        case Some(str) => if (str.getType.toString == TLangString.getType) println(str.asInstanceOf[TLangString].getElement)
        case None =>
      }
      Right(None)
    })
  ))
  ))
}
