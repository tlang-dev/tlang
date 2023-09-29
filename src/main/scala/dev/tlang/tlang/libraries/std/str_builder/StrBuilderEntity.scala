package dev.tlang.tlang.libraries.std.str_builder

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{LazyValue, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef, ModelSetRefCurrying}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}

class StrBuilderEntity(strBuilder: StringBuilder) {

  val addFunc: HelperFunc = HelperFunc(None, "add", Some(List(HelperCurrying(None, List(HelperParam(None, Some("_var1"), ObjType(None, None, TLangString.getType)))))), None, HelperContent(None, Some(List(
    HelperInternalFunc((context: Context) => {
      ContextUtils.findVar(context, "_var1") match {
        case Some(str) =>
          if (str.getType == TLangString.getType)
            strBuilder ++= str.asInstanceOf[TLangString].toString
        case None =>
      }
      Right(None)
    })
  ))))

  val toStringFunc: HelperFunc = HelperFunc(None, "toString", Some(List(HelperCurrying(None, List()))), Some(List(ObjType(None, None, TLangString.getType))), HelperContent(None, Some(List(
    HelperInternalFunc((context: Context) => {
      Right(Some(List(new TLangString(None, strBuilder.toString()))))
    })
  ))))

  val entity: ModelSetEntity = ModelSetEntity(None, "StrBuilder", None, None,
    Some(List(
      ModelSetAttribute(None, Some("add"), ModelSetRef(None, List("add"), Some(List(ModelSetRefCurrying(None, List(Operation(None, None, Right(LazyValue(None, None, Some(TLangString)))))))), Some(Left(addFunc)))),
      ModelSetAttribute(None, Some("toString"), ModelSetRef(None, List("toString"), None, Some(Left(toStringFunc))))
    )))

}