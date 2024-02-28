package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.AstTmplNode
import tlang.core.Type

trait LangCallObjType[TYPE] extends AstTmplNode

object LangCallObjType {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)
}
