package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.lang.ast.LangNode

trait LangBlock[T] extends LangNode[T] with DomainBlock {

  def getParams: Option[List[HelperParam]]

  def getLang: String

  def getScope: Scope

  def getName: String

}
