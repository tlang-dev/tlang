package dev.tlang.tlang.libraries.tio.file

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.libraries.ModulePattern

object IOFileModule extends ModulePattern {

  override def getName: String = "IOFile"

  override def getProject: String = "IO"

  override def getFunctions: List[HelperFunc] = List(
    IOFile.writeFunc
  )
}
