package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.common.{ArrayType, ObjType, ValueType}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource

import scala.jdk.CollectionConverters._

object BuildCommon {

  def buildAssignVar(resource: ContextResource, assign: AssignVarContext): AssignVar = {
    val varType = if (assign.`type` != null && assign.`type`.getText.nonEmpty) Some(BuildCommon.buildValueType(resource, assign.valueType())) else None
    AssignVar(addContext(resource, assign), assign.name.getText,
      varType,
      buildOperation(resource, varType, assign.value))
  }

  //  def buildSimpleValueType(resource: ContextResource, `type`: Option[String] = None, valueType: SimpleValueTypeContext): SimpleValueStatement[_] = {
  //    valueType match {
  //      case call@_ if call.callObj() != null => BuildHelperStatement.buildCallObject(resource, call.callObj())
  //      case value@_ if value.primitiveValue() != null => buildPrimitiveValue(resource, `type`, value.primitiveValue())
  //    }
  //  }

  def buildOperation(resource: ContextResource, expectedType: Option[ValueType], operation: OperationContext): Operation = {
    val content: Either[Operation, ComplexValueStatement[_]] =
      if (operation.content != null && !operation.content.isEmpty) Right(buildComplexValueType(resource, expectedType, operation.content))
      else Left(buildOperation(resource, expectedType, operation.innerBlock))
    val next =
      if (operation.op != null && !operation.op.isEmpty) Some((buildOperator(operation.op.getText), buildOperation(resource, expectedType, operation.next)))
      else None
    Operation(None, expectedType, content, next)
  }

  def buildOperator(opType: String): Operator.operator = {
    opType match {
      case "&&" => Operator.AND
      case "||" => Operator.OR
      case "+" => Operator.ADD
      case "-" => Operator.SUBTRACT
      case "*" => Operator.MULTIPLY
      case "/" => Operator.DIVIDE
      case "%" => Operator.MODULO
      case "==" => Operator.EQUAL
      case "!=" => Operator.NOT_EQUAL
      case ">" => Operator.GREATER
      case "<" => Operator.LESSER
      case ">=" => Operator.GREATER_OR_EQUAL
      case "<=" => Operator.LESSER_OR_EQUAL
    }
  }

  def buildComplexValueType(resource: ContextResource, `type`: Option[ValueType] = None, valueType: ComplexValueTypeContext): ComplexValueStatement[_] = {
    valueType match {
      case call@_ if call.callObj() != null => BuildHelperStatement.buildCallObject(resource, call.callObj())
      case value@_ if value.primitiveValue() != null => buildPrimitiveValue(resource, `type`, value.primitiveValue())
      case multi@_ if multi.multiValue() != null => buildMultiValue(resource, multi.multiValue())
      case lazyVal@_ if lazyVal.lazyValue() != null => LazyValue(addContext(resource, lazyVal.lazyValue()), None, None)
      case impl@_ if impl.impl() != null => EntityImpl(addContext(resource, impl.impl()), None, AstBuilderUtils.getText(impl.impl().`type`), buildComplexAttributes(resource, impl.impl().attrs.asScala.toList))
    }
  }

  def buildPrimitiveValue(resource: ContextResource, `type`: Option[ValueType] = None, value: PrimitiveValueContext): PrimitiveValue[_] = {
    value match {
      case string@_ if string.stringValue() != null => new TLangString(addContext(resource, string.stringValue()), AstBuilderUtils.extraString(string.stringValue().value.getText))
      case number@_ if number.numberValue() != null =>
        val numbVal = number.numberValue().value.getText
        if (numbVal.contains(".")) new TLangDouble(addContext(resource, number.numberValue()), numbVal.toDouble) else new TLangLong(addContext(resource, number.numberValue()), numbVal.toLong)
      case text@_ if text.textValue() != null => new TLangString(addContext(resource, text.textValue()), AstBuilderUtils.extraText(text.textValue().value.getText))
      case entity@_ if entity.entityValue() != null => buildEntityValue(resource, `type`, entity.entityValue())
      case bool@_ if bool.boolValue() != null => new TLangBool(addContext(resource, bool.boolValue()), bool.boolValue().value.getText == "true")
      case array@_ if array.arrayValue() != null => buildArray(resource, array.arrayValue())
    }
  }

  def buildArray(resource: ContextResource, array: ArrayValueContext): ArrayValue = {
    ArrayValue(addContext(resource, array), if (array.params != null) buildComplexAttributes(resource, array.params.asScala.toList) else None)
  }

  //  def buildSimpleValueTypes(resource: ContextResource, types: List[SimpleValueTypeContext]): Option[List[SimpleValueStatement[_]]] = {
  //    if (types.nonEmpty) Some(types.map(valType => buildSimpleValueType(resource, None, valType))) else None
  //  }

  def buildEntityValue(resource: ContextResource, `type`: Option[ValueType] = None, entity: EntityValueContext): EntityValue = {
    EntityValue(addContext(resource, entity), `type`,
      buildComplexAttributes(resource, entity.attrs.asScala.toList))
  }

  def buildMultiValue(resource: ContextResource, multi: MultiValueContext): MultiValue = {
    MultiValue(addContext(resource, multi), multi.values.asScala.toList.map(buildOperation(resource, None, _)))
  }

  //  def buildSimpleAttributes(resource: ContextResource, attrs: List[SimpleAttributeContext]): Option[List[SimpleAttribute]] = {
  //    if (attrs.nonEmpty) Some(attrs.map(attr => buildSimpleAttribute(resource, attr))) else None
  //  }
  //
  //  def buildSimpleAttribute(resource: ContextResource, attr: SimpleAttributeContext): SimpleAttribute = {
  //    val attrType = if (attr.`type` != null && attr.`type`.getText.nonEmpty) Some(attr.`type`.getText) else None
  //    SimpleAttribute(addContext(resource, attr),
  //      if (attr.attr != null && attr.attr.getText.nonEmpty) Some(attr.attr.getText) else None,
  //      attrType,
  //      buildSimpleValueType(resource, attrType, attr.value)
  //    )
  //  }

  def buildComplexAttributes(resource: ContextResource, attrs: List[ComplexAttributeContext]): Option[List[ComplexAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(attr => buildComplexAttribute(resource, attr))) else None
  }

  def buildComplexAttribute(resource: ContextResource, attr: ComplexAttributeContext): ComplexAttribute = {
    val attrType = if (attr.`type` != null && attr.`type`.getText.nonEmpty) Some(buildValueType(resource, attr.valueType()))
    else None
    ComplexAttribute(addContext(resource, attr),
      if (attr.attr != null && attr.attr.getText.nonEmpty) Some(attr.attr.getText) else None,
      attrType,
      buildOperation(resource, attrType, attr.value)
    )
  }

  def buildValueType(resource: ContextResource, valueType: ValueTypeContext): ValueType = {
    valueType match {
      case obj@_ if obj.objType() != null => buildObjType(resource, obj.objType())
      case array@_ if array.arrayType() != null => buildArrayType(resource, array.arrayType())

    }
  }

  def buildObjType(resource: ContextResource, objType: ObjTypeContext): ObjType = {
    ObjType(addContext(resource, objType),
      if (objType.exTpye != null && objType.exTpye.getText.nonEmpty) Some(objType.exTpye.getText) else None,
      objType.`type`.getText)
  }

  def buildArrayType(resource: ContextResource, arrayType: ArrayTypeContext): ArrayType = {
    ArrayType(addContext(resource, arrayType),
      if (arrayType.exTpye != null && arrayType.exTpye.getText.nonEmpty) Some(arrayType.exTpye.getText) else None,
      arrayType.`type`.getText)
  }

}
