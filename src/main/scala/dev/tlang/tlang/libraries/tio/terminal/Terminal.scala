package dev.tlang.tlang.libraries.tio.terminal

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}

object Terminal {

  def printlnFunc: HelperFunc = HelperFunc("println", Some(List(HelperCurrying(List(HelperParam(Some("str"), HelperObjType(TLangString.getType)))))), None, HelperContent(Some(List(
    HelperInternalFunc((context: Context) => {
      ContextUtils.findVar(context, "str") match {
        case Some(str) => if (str.getType == TLangString.getType) println(str.asInstanceOf[TLangString].getValue)
        case None =>
      }
      Right(None)
    })
  ))
  ))
}
