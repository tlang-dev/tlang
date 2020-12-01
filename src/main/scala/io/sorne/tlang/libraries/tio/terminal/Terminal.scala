package io.sorne.tlang.libraries.tio.terminal

import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.ast.helper._
import io.sorne.tlang.interpreter.context.{Context, ContextUtils}

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
