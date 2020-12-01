package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.common.call._
import io.sorne.tlang.ast.common.condition.{Condition, ConditionBlock}
import io.sorne.tlang.ast.helper._
import org.antlr.v4.runtime.Token

import scala.jdk.CollectionConverters._

object BuildHelperStatement {

  def build(statements: List[HelperStatementContext]): List[HelperStatement] = {
    statements.map {
      case statement@_ if statement.callObj() != null => buildCallObject(statement.callObj())
      case statement@_ if statement.helperIf() != null => buildIf(statement.helperIf())
      case statement@_ if statement.helperFor() != null => buildFor(statement.helperFor())
    }
  }

  def buildCallObject(call: CallObjContext): CallObject = {
    CallObject(call.objs.asScala.toList.map {
      case obj@_ if obj.callVariable() != null => CallVarObject(obj.callVariable().name.getText)
      case obj@_ if obj.callArray() != null => CallArrayObject(obj.callArray().name.getText, BuildCommon.buildSimpleValueType(obj.callArray().elem))
      case obj@_ if obj.callFunc() != null => buildCallFunc(obj.callFunc())
    })
  }

  def buildCallFunc(func: CallFuncContext): CallFuncObject = {
    CallFuncObject(if (func.name != null) Some(func.name.getText) else None,
      buildCallFuncParam(func.currying.asScala.toList))
  }

  def buildCallFuncParam(params: List[CurryParamsContext]):Option[List[CallFuncParam]] = {
    if(params.nonEmpty) Some(params.map(param => CallFuncParam(BuildCommon.buildComplexAttributes(param.params.asScala.toList)))) else None
  }

  def buildIf(anIf: HelperIfContext): HelperIf = {
    HelperIf(buildConditionBlock(anIf.cond),
      if (!anIf.body.content.isEmpty) Some(BuildHelperBlock.buildContent(anIf.body)) else None,
      if (anIf.orElse != null) Some(BuildHelperBlock.buildContent(anIf.orElse.body)) else None)
  }

  def buildConditionBlock(block: ConditionBlockContext): ConditionBlock = {
    ConditionBlock(buildEitherCondition(block),
      if (block.link != null) Some(buildConditionLink(block.link)) else None,
      if (block.next != null) Some(buildConditionBlock(block.next)) else None)
  }

  def buildEitherCondition(block: ConditionBlockContext): Either[ConditionBlock, Condition] = {
    if (block.innerBlock != null) Left(buildConditionBlock(block.innerBlock))
    else Right(buildCondition(block.content))
  }

  def buildCondition(condition: ConditionContext): Condition = {
    Condition(BuildCommon.buildSimpleValueType(condition.arg1),
      if (condition.mark != null) Some(buildConditionType(condition.mark)) else None,
      if (condition.arg2 != null) Some(BuildCommon.buildSimpleValueType(condition.arg2)) else None,
      if (condition.link != null) Some(buildConditionLink(condition.link)) else None,
      if (condition.next != null) Some(buildConditionBlock(condition.next)) else None)
  }

  def buildConditionLink(link: Token): ConditionLink.condition = {
    link.getText match {
      case "&&" => ConditionLink.AND
      case "||" => ConditionLink.OR
    }
  }

  def buildConditionType(condition: ConditionMarkContext): ConditionType.condition = {
    condition.getText match {
      case "==" => ConditionType.EQUAL
      case "!=" => ConditionType.NOT_EQUAL
      case ">" => ConditionType.GREATER
      case "<" => ConditionType.LESSER
      case ">=" => ConditionType.GREATER_OR_EQUAL
      case "<=" => ConditionType.LESSER_OR_EQUAL
    }
  }

  def buildFor(aFor: HelperForContext): HelperFor = {
    HelperFor(aFor.`var`.getText,
      if (aFor.start != null) Some(BuildCommon.buildSimpleValueType(aFor.start)) else None,
      buildForType(aFor.`type`),
      BuildCommon.buildSimpleValueType(aFor.array),
      BuildHelperBlock.buildContent(aFor.body)
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
