package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import tlang.core.Type
import tlang.internal.TmplNode

trait LangCallObjType[TYPE] extends TmplNode[TYPE]

object LangCallObjType {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)
}
