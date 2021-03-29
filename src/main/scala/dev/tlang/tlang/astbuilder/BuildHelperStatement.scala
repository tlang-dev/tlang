package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.common.call
import dev.tlang.tlang.ast.common.call.{SetAttribute, _}
import dev.tlang.tlang.ast.common.value.{ArrayValue, TLangBool, TLangLong}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import org.antlr.v4.runtime.Token

import scala.jdk.CollectionConverters._

object BuildHelperStatement {

  def build(resource: ContextResource, statements: List[HelperStatementContext]): List[HelperStatement] = {
    statements.map {
      case statement@_ if statement.assignVar() != null => BuildCommon.buildAssignVar(resource, statement.assignVar())
      case statement@_ if statement.operation() != null => BuildCommon.buildOperation(resource, None, statement.operation())
      case statement@_ if statement.helperIf() != null => buildIf(resource, statement.helperIf())
      case statement@_ if statement.helperFor() != null => buildFor(resource, statement.helperFor())
    }
  }

  def buildCallObject(resource: ContextResource, call: CallObjContext): CallObject = {
    CallObject(addContext(resource, call), call.objs.asScala.toList.map {
      case obj@_ if obj.callVariable() != null => CallVarObject(addContext(resource, obj.callVariable()), obj.callVariable().name.getText)
      case obj@_ if obj.callArray() != null => CallArrayObject(addContext(resource, obj.callArray()), obj.callArray().name.getText, BuildCommon.buildOperation(resource, None, obj.callArray().elem))
      case obj@_ if obj.callFunc() != null && obj.ref == null => buildCallFunc(resource, obj.callFunc())
      case obj@_ if obj.callFunc() != null && obj.ref != null => buildCallRefFunc(resource, obj.callFunc())
    })
  }

  def buildCallFunc(resource: ContextResource, func: CallFuncContext): CallFuncObject = {
    CallFuncObject(addContext(resource, func), if (func.name != null) Some(func.name.getText) else None,
      buildCallFuncParam(resource, func.currying.asScala.toList))
  }

  def buildCallRefFunc(resource: ContextResource, func: CallFuncContext): CallRefFuncObject = {
    CallRefFuncObject(addContext(resource, func), if (func.name != null) Some(func.name.getText) else None,
      buildCallFuncParam(resource, func.currying.asScala.toList))
  }

  def buildCallFuncParam(resource: ContextResource, params: List[CurryParamsContext]): Option[List[CallFuncParam]] = {
    if (params.nonEmpty) Some(params.map(param => CallFuncParam(addContext(resource, param),
      if (param.params != null && !param.params.isEmpty) Some(param.params.asScala.toList.map(param => buildSetAttribute(resource, param))) else None)))
    else None
  }

  def buildSetAttribute(resource: ContextResource, attr: SetAttributeContext): SetAttribute = {
    call.SetAttribute(addContext(resource, attr), AstBuilderUtils.getText(attr.attr), BuildCommon.buildOperation(resource, None, attr.value))
  }

  def buildIf(resource: ContextResource, anIf: HelperIfContext): HelperIf = {
    HelperIf(addContext(resource, anIf), BuildCommon.buildOperation(resource, Some(TLangBool.getType), anIf.cond),
      if (!anIf.body.content.isEmpty) Some(BuildHelperBlock.buildContent(resource, anIf.body)) else None,
      if (anIf.orElse != null) Some(BuildHelperBlock.buildContent(resource, anIf.orElse.body)) else None)
  }

  //  def buildConditionBlock(resource: ContextResource, block: ConditionBlockContext): ConditionBlock = {
  //    ConditionBlock(addContext(resource, block), buildEitherCondition(resource, block),
  //      if (block.link != null) Some(buildConditionLink(block.link)) else None,
  //      if (block.next != null) Some(buildConditionBlock(resource, block.next)) else None)
  //  }
  //
  //  def buildEitherCondition(resource: ContextResource, block: ConditionBlockContext): Either[ConditionBlock, Condition] = {
  //    if (block.innerBlock != null) Left(buildConditionBlock(resource, block.innerBlock))
  //    else Right(buildCondition(resource, block.content))
  //  }
  //
  //  def buildCondition(resource: ContextResource, condition: ConditionContext): Condition = {
  //    Condition(addContext(resource, condition), BuildCommon.buildSimpleValueType(resource, None, condition.arg1),
  //      if (condition.mark != null) Some(buildConditionType(condition.mark.getText)) else None,
  //      if (condition.arg2 != null) Some(BuildCommon.buildSimpleValueType(resource, None, condition.arg2)) else None,
  //      if (condition.link != null) Some(buildConditionLink(condition.link)) else None,
  //      if (condition.next != null) Some(buildConditionBlock(resource, condition.next)) else None)
  //  }

  //  def buildConditionLink(link: Token): Operator.condition = {
  //    link.getText match {
  //      case "&&" => Operator.AND
  //      case "||" => Operator.OR
  //    }
  //  }
  //
  //  def buildConditionType(condType: String): ConditionType.condition = {
  //    condType match {
  //      case "==" => ConditionType.EQUAL
  //      case "!=" => ConditionType.NOT_EQUAL
  //      case ">" => ConditionType.GREATER
  //      case "<" => ConditionType.LESSER
  //      case ">=" => ConditionType.GREATER_OR_EQUAL
  //      case "<=" => ConditionType.LESSER_OR_EQUAL
  //    }
  //  }

  def buildFor(resource: ContextResource, aFor: HelperForContext): HelperFor = {
    HelperFor(addContext(resource, aFor), aFor.`var`.getText,
      if (aFor.start != null) Some(BuildCommon.buildOperation(resource, Some(TLangLong.getType), aFor.start)) else None,
      buildForType(aFor.`type`),
      BuildCommon.buildOperation(resource, Some(ArrayValue.getType), aFor.array),
      BuildHelperBlock.buildContent(resource, aFor.body)
    )
  }

  def buildForType(forType: Token): ForType.forType = {
    forType.getText match {
      case "in" => ForType.IN
      case "to" => ForType.TO
      case "until" => ForType.UNTIL
    }
  }

}
