package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope
import tlang.internal.AnyTmplBlock

trait AnyTmplInterpretedBlock[T] extends AnyTmplBlock[T] {

  def getParams: Option[List[HelperParam]]

  def getLangs: List[String]

  def getScope: Scope

  def getName: String

}

object AnyTmplInterpretedBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")
}
