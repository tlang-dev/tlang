package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.helper._

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
      case obj@_ if obj.helperCallArray() != null => HelperCallArrayObject(obj.helperCallArray().name.getText, obj.helperCallArray().elem.getText)
      case obj@_ if obj.helperCallFunc() != null => buildCallFunc(obj.helperCallFunc())
    })
  }

  def buildCallFunc(func: HelperCallFuncContext): HelperCallFuncObject = {
    HelperCallFuncObject(if(func.name != null)Some(func.name.getText) else None,
      None)
  }

  def buildIf(anIf: HelperIfContext): HelperIf = {
   null
  }

  def buildFor(aFor: HelperForContext): HelperFor = {
    HelperFor()
  }

}
