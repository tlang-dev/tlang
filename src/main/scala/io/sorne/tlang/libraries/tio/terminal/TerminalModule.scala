package io.sorne.tlang.libraries.tio.terminal

import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.libraries.ModulePattern

object TerminalModule extends ModulePattern {

  override def getName: String = "Terminal"

  override def getProject: String = "IO"

  override def getFunctions: List[HelperFunc] = List(
    Terminal.printlnFunc
  )
}
