package dev.tlang.tlang.libraries.std.entity

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangBool, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}

object StdEntity {

  def existsFunc: HelperFunc = HelperFunc(None, "exists",
    Some(List(HelperCurrying(None, List(HelperParam(None, Some("name"), ObjType(None, None, TLangString.getType)), HelperParam(None, Some("entity"), ObjType(None, None, EntityValue.getClass.getSimpleName)))))),
    Some(List(ObjType(None, None, TLangBool.getType))),
    HelperContent(None, Some(List(
      HelperInternalFunc((context: Context) => {
        ContextUtils.findVar(context, "name") match {
          case Some(name) =>
            ContextUtils.findVar(context, "entity") match {
              case Some(paramEntity) =>
                if (paramEntity.isInstanceOf[EntityValue]) {
                  val entity = paramEntity.asInstanceOf[EntityValue]
                  if (entity.attrs.isDefined) {
                    Right(Some(List(new TLangBool(None, entity.attrs.get.exists(attr => {
                      if (attr.attr.isDefined) attr.attr.get == name.asInstanceOf[TLangString].toString
                      else false
                    })))))
                  }
                  else Right(Some(List(new TLangBool(None, false))))
                } else Right(Some(List(new TLangBool(None, false))))
              case None => Right(Some(List(new TLangBool(None, false))))
            }
          case None => Right(Some(List(new TLangBool(None, false))))
        }
      })
    ))))

}
