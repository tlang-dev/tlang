package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.common.call.{ComplexValueStatement, SimpleValueStatement}
import dev.tlang.tlang.ast.common.value._

import scala.jdk.CollectionConverters._

object BuildCommon {

  def buildAssignVar(assign: AssignVarContext): AssignVar = {
    val varType = if (assign.`type` != null && assign.`type`.getText.nonEmpty) Some(assign.`type`.getText) else None
    AssignVar(assign.name.getText,
      varType,
      buildComplexValueType(varType, assign.value))
  }

  def buildSimpleValueType(`type`: Option[String] = None, valueType: SimpleValueTypeContext): SimpleValueStatement[_] = {
    valueType match {
      case call@_ if call.callObj() != null => BuildHelperStatement.buildCallObject(call.callObj())
      case value@_ if value.primitiveValue() != null => buildPrimitiveValue(`type`, value.primitiveValue())
    }
  }

  def buildComplexValueType(`type`: Option[String] = None, valueType: ComplexValueTypeContext): ComplexValueStatement[_] = {
    valueType match {
      case call@_ if call.callObj() != null => BuildHelperStatement.buildCallObject(call.callObj())
      case value@_ if value.primitiveValue() != null => buildPrimitiveValue(`type`, value.primitiveValue())
      case condition@_ if condition.conditionBlock() != null => BuildHelperStatement.buildConditionBlock(condition.conditionBlock())
      case multi@_ if multi.multiValue() != null => buildMultiValue(multi.multiValue())
      case lazyVal@_ if lazyVal.lazyValue() != null => LazyValue(None, None)
    }
  }

  def buildPrimitiveValue(`type`: Option[String] = None, value: PrimitiveValueContext): PrimitiveValue[_] = {
    value match {
      case string@_ if string.stringValue() != null => new TLangString(AstBuilderUtils.extraString(string.stringValue().value.getText))
      case number@_ if number.numberValue() != null =>
        val numbVal = number.numberValue().value.getText
        if (numbVal.contains(".")) new TLangDouble(numbVal.toDouble) else new TLangLong(numbVal.toLong)
      case text@_ if text.textValue() != null => new TLangString(AstBuilderUtils.extraText(text.textValue().value.getText))
      case entity@_ if entity.entityValue() != null => buildEntityValue(`type`, entity.entityValue())
      case bool@_ if bool.boolValue() != null => new TLangBool(bool.boolValue().value.getText == "true")
      case array@_ if array.arrayValue() != null => buildArray(array.arrayValue())
    }
  }

  def buildArray(array: ArrayValueContext): ArrayValue = {
    ArrayValue(if (array.params != null) buildSimpleAttributes(array.params.asScala.toList) else None)
  }

  def buildSimpleValueTypes(types: List[SimpleValueTypeContext]): Option[List[SimpleValueStatement[_]]] = {
    if (types.nonEmpty) Some(types.map(valType => buildSimpleValueType(None, valType))) else None
  }

  def buildEntityValue(`type`: Option[String] = None, entity: EntityValueContext): EntityValue = {
    EntityValue(if (`type`.isDefined) Some(`type`.get) else None,
      buildComplexAttributes(entity.attrs.asScala.toList), buildComplexAttributes(entity.decl.asScala.toList))
  }

  def buildMultiValue(multi: MultiValueContext): MultiValue = {
    MultiValue(multi.values.asScala.toList.map(buildComplexValueType(None, _)))
  }

  def buildSimpleAttributes(attrs: List[SimpleAttributeContext]): Option[List[SimpleAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(buildSimpleAttribute)) else None
  }

  def buildSimpleAttribute(attr: SimpleAttributeContext): SimpleAttribute = {
    val attrType = if (attr.`type` != null && attr.`type`.getText.nonEmpty) Some(attr.`type`.getText) else None
    SimpleAttribute(
      if (attr.attr != null && attr.attr.getText.nonEmpty) Some(attr.attr.getText) else None,
      attrType,
      buildSimpleValueType(attrType, attr.value)
    )
  }

  def buildComplexAttributes(attrs: List[ComplexAttributeContext]): Option[List[ComplexAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(buildComplexAttribute)) else None
  }

  def buildComplexAttribute(attr: ComplexAttributeContext): ComplexAttribute = {
    val attrType = if (attr.`type` != null && attr.`type`.getText.nonEmpty) Some(attr.`type`.getText) else None
    ComplexAttribute(
      if (attr.attr != null && attr.attr.getText.nonEmpty) Some(attr.attr.getText) else None,
      attrType,
      buildComplexValueType(attrType, attr.value)
    )
  }

}
