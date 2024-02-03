package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope

trait AnyTmplBlock[T] extends TmplNode[T] with DomainBlock {

  def getParams: Option[List[HelperParam]]

  def getLangs: List[String]

  def getScope: Scope

  def getName: String

}

object AnyTmplBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")
}
