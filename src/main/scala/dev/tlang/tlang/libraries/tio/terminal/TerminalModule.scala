package dev.tlang.tlang.libraries.tio.terminal

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.libraries.ModulePattern

object TerminalModule extends ModulePattern {

  override def getName: String = "Terminal"

  override def getProject: String = "IO"

  override def getFunctions: List[HelperFunc] = List(
    Terminal.printlnFunc
  )
}
