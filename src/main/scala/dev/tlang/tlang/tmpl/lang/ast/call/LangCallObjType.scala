package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.tmpl.TmplNode

trait LangCallObjType[TYPE] extends TmplNode[TYPE]

object LangCallObjType {
  val name: String = this.getClass.getSimpleName.replace("$", "")
}
