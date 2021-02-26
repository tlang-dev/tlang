package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.helper.HelperFunc

object BuiltIntLibs {

  val buildIntLibs: Map[String, HelperFunc] = Map(
    "forEach" -> TmplFor.tmplForFunc
  )

}
