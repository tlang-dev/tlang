package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.common.call.{ComplexValueStatement, SimpleValueStatement}
import io.sorne.tlang.ast.common.value._

import scala.jdk.CollectionConverters._

object BuildCommon {

  def buildAssignVar(assign: AssignVarContext): AssignVar = {
    AssignVar(assign.name.getText, buildComplexValueType(assign.value))
  }

  def buildSimpleValueType(valueType: SimpleValueTypeContext): SimpleValueStatement[_] = {
    valueType match {
      case call@_ if call.callObj() != null => BuildHelperStatement.buildCallObject(call.callObj())
      case value@_ if value.primitiveValue() != null => buildPrimitiveValue(value.primitiveValue())
    }
  }

  def buildComplexValueType(valueType: ComplexValueTypeContext): ComplexValueStatement[_] = {
    valueType match {
      case call@_ if call.callObj() != null => BuildHelperStatement.buildCallObject(call.callObj())
      case value@_ if value.primitiveValue() != null => buildPrimitiveValue(value.primitiveValue())
      case condition@_ if condition.conditionBlock() != null => BuildHelperStatement.buildConditionBlock(condition.conditionBlock())
    }
  }

  def buildPrimitiveValue(value: PrimitiveValueContext): PrimitiveValue[_] = {
    value match {
      case string@_ if string.stringValue() != null => new TLangString(string.stringValue().value.getText)
      case number@_ if number.numberValue() != null =>
        val numbVal = number.numberValue().value.getText
        if (numbVal.contains(".")) new TLangDouble(numbVal.toDouble) else new TLangLong(numbVal.toLong)
      case text@_ if text.textValue() != null => new TLangString(text.textValue().value.getText)
      case entity@_ if entity.entityValue() != null => buildEntityValue(entity.entityValue())
      case bool@_ if bool.boolValue() != null => new TLangBool(bool.boolValue().value.getText == "true")
      case array@_ if array.arrayValue() != null => buildArray(array.arrayValue())
    }
  }

  def buildArray(array: ArrayValueContext): ArrayValue = {
    ArrayValue(if (array.params != null) buildSimpleAttributes(array.params.asScala.toList) else None)
  }

  def buildSimpleValueTypes(types: List[SimpleValueTypeContext]): Option[List[SimpleValueStatement[_]]] = {
    if (types.nonEmpty) Some(types.map(buildSimpleValueType)) else None
  }

  def buildEntityValue(entity: EntityValueContext): EntityValue = {
    EntityValue(None, buildComplexAttributes(entity.attrs.asScala.toList), buildComplexAttributes(entity.decl.asScala.toList))
  }

  def buildSimpleAttributes(attrs: List[SimpleAttributeContext]): Option[List[SimpleAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(buildSimpleAttribute)) else None
  }

  def buildSimpleAttribute(attr: SimpleAttributeContext): SimpleAttribute = {
    SimpleAttribute(
      if (attr.attr != null && !attr.attr.getText.isEmpty) Some(attr.attr.getText) else None,
      buildSimpleValueType(attr.value)
    )
  }

  def buildComplexAttributes(attrs: List[ComplexAttributeContext]): Option[List[ComplexAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(buildComplexAttribute)) else None
  }

  def buildComplexAttribute(attr: ComplexAttributeContext): ComplexAttribute = {
    ComplexAttribute(
      if (attr.attr != null && !attr.attr.getText.isEmpty) Some(attr.attr.getText) else None,
      buildComplexValueType(attr.value)
    )
  }

}
