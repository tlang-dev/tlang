package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.helper.call._
import org.antlr.v4.runtime.Token

import scala.jdk.CollectionConverters._

object BuildHelperStatement {

  def build(statements: List[HelperStatementContext]): List[HelperStatement] = {
    statements.map {
      case statement@_ if statement.helperCallObj() != null => buildCallObject(statement.helperCallObj())
      case statement@_ if statement.helperIf() != null => buildIf(statement.helperIf())
      case statement@_ if statement.helperFor() != null => buildFor(statement.helperFor())
    }
  }

  def buildCallObject(call: HelperCallObjContext): HelperCallObject = {
    HelperCallObject(call.objs.asScala.toList.map {
      case obj@_ if obj.helperCallVariable() != null => HelperCallVarObject(obj.helperCallVariable().name.getText)
      case obj@_ if obj.helperCallArray() != null => HelperCallArrayObject(obj.helperCallArray().name.getText, buildCallObject(obj.helperCallArray().elem))
      case obj@_ if obj.helperCallFunc() != null => buildCallFunc(obj.helperCallFunc())
      case obj@_ if obj.helperCallNumber() != null => HelperCallInt(obj.getText.toInt)
      case obj@_ if obj.helperCallString() != null => HelperCallString(obj.getText)
    })
  }

  def buildCallFunc(func: HelperCallFuncContext): HelperCallFuncObject = {
    HelperCallFuncObject(if (func.name != null) Some(func.name.getText) else None,
      None)
  }

  def buildIf(anIf: HelperIfContext): HelperIf = {
    HelperIf(buildConditionBlock(anIf.condition),
      if (!anIf.body.content.isEmpty) Some(BuildHelperBlock.buildContent(anIf.body)) else None,
      if (anIf.orElse != null) Some(BuildHelperBlock.buildContent(anIf.orElse.body)) else None)
  }

  def buildConditionBlock(block: HelperConditionBlockContext): HelperConditionBlock = {
    HelperConditionBlock(buildEitherCondition(block),
      if (block.link != null) Some(buildConditionLink(block.link)) else None,
      if (block.next != null) Some(buildConditionBlock(block.next)) else None)
  }

  def buildEitherCondition(block: HelperConditionBlockContext): Either[HelperConditionBlock, HelperCondition] = {
    if (block.innerBlock != null) Left(buildConditionBlock(block.innerBlock))
    else Right(buildCondition(block.content))
  }

  def buildCondition(condition: HelperConditionContext): HelperCondition = {
    HelperCondition(buildCallObject(condition.arg1),
      if (condition.mark != null) Some(buildConditionType(condition.mark)) else None,
      if (condition.arg2 != null) Some(buildCallObject(condition.arg2)) else None,
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
      if (aFor.start != null) Some(buildCallObject(aFor.start)) else None,
      buildForType(aFor.`type`),
      buildCallObject(aFor.array),
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
