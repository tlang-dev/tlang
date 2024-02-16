package dev.tlang.tlang.tmpl.lang.ast.call

import tlang.internal.TmplNode
import tlang.internal.TmplNode

trait LangCallObjType[TYPE] extends TmplNode[TYPE]

object LangCallObjType {
  val name: String = this.getClass.getSimpleName.replace("$", "")
}
