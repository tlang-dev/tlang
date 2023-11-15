package dev.tlang.tlang.astbuilder.tmpl

import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, TLangString}

object LangEntityUtils {

  def findStrValue(entity: EntityValue): String = {
    findAttribute(entity, "value") match {
      case Some(value) => findSimpleValue[TLangString](value) match {
        case Some(value) => value.toString
        case None => ""
      }
      case None => ""
    }
  }

  def findAttribute(entity: EntityValue, name: String): Option[ComplexAttribute] = {
    if (entity.attrs.isDefined)
      entity.attrs.get.find(attrs => attrs.attr.getOrElse("") == name)
    else None
  }

  def findSimpleValue[T](attr: Option[ComplexAttribute]): Option[T] = {
    if (attr.isDefined) findSimpleValue(attr.get)
    None
  }

  def findSimpleValue[T](attr: ComplexAttribute): Option[T] = {
    attr.value.content match {
      case Left(_) => None
      case Right(value) => Some(value.asInstanceOf[T])
    }
  }
}
