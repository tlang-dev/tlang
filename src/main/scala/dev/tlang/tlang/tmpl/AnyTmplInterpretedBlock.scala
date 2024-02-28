package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope

trait AnyTmplInterpretedBlock[T] extends AstAnyTmplBlock {

  def getParams: Option[List[HelperParam]]

  def getScope: Scope

  //  def getName: String

}

object AnyTmplInterpretedBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelType: ManualType = ManualType(this.getClass.getPackageName, this.name)
}
