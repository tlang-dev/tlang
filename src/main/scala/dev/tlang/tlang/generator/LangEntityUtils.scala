package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, TLangString}
import tlang.core.Value

object LangEntityUtils {

  def findStrValue(entity: EntityValue): String = {
    findAttribute(entity, "value") match {
      case Some(value) => findSimpleValue(value) match {
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

  def findSimpleValueOptional(attr: Option[ComplexAttribute]): Option[Value] = {
    if (attr.isDefined) findSimpleValue(attr.get)
    None
  }

  def findSimpleValue(attr: ComplexAttribute): Option[Value] = {
    attr.value.content match {
      case Left(_) => None
      case Right(value) => Some(value.asInstanceOf[Value])
    }
  }
}
