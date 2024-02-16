package dev.tlang.tlang.libraries.std.entity

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangBool, TLangString}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import tlang.core.{Bool, Null}

object StdEntity {

  def existsFunc: HelperFunc = HelperFunc(Null.empty(), "exists",
    Some(List(HelperCurrying(Null.empty(), List(HelperParam(Null.empty(), Some("name"), ObjType(Null.empty(), None, TLangString.getType)), HelperParam(Null.empty(), Some("entity"), ObjType(Null.empty(), None, EntityValue.getClass.getSimpleName)))))),
    Null.of(List(ObjType(Null.empty(), None, TLangBool.getType))),
    HelperContent(Null.empty(), Some(List(
      HelperInternalFunc((context: Context) => {
        ContextUtils.findVar(context, "name") match {
          case Some(name) =>
            ContextUtils.findVar(context, "entity") match {
              case Some(paramEntity) =>
                if (paramEntity.isInstanceOf[EntityValue]) {
                  val entity = paramEntity.asInstanceOf[EntityValue]
                  if (entity.attrs.isDefined) {
                    Right(Some(List(new TLangBool(Null.empty(), new Bool(entity.attrs.get.exists(attr => {
                      if (attr.attr.isDefined) attr.attr.get == name.asInstanceOf[TLangString].toString
                      else false
                    }))))))
                  }
                  else Right(Some(List(new TLangBool(Null.empty(), new Bool(false)))))
                } else Right(Some(List(new TLangBool(Null.empty(), new Bool(false)))))
              case None => Right(Some(List(new TLangBool(Null.empty(), new Bool(false)))))
            }
          case None => Right(Some(List(new TLangBool(Null.empty(), new Bool(false)))))
        }
      })
    ))))

}
