package dev.tlang.tlang.libraries

import dev.tlang.tlang.libraries.tio.terminal.TerminalModule
import io.sorne.tlang.libraries.generator.GenModule
import io.sorne.tlang.libraries.tio.terminal.TerminalModule
import io.sorne.tlang.loader.Module
import io.sorne.tlang.loader.manifest.{Dependency, Stability}

object Modules {

  val organisation = "TLang"
  val version = "1.0.0"
  val stability: Stability.Value = Stability.ALPHA
  val releaseNumber = 1

  val tLangModules: Map[String, Module] = Map(
    TerminalModule.getModuleName -> TerminalModule.getModule,
    GenModule.getModuleName -> GenModule.getModule,
  )

  def findModule(dependency: Dependency): Option[Module] = {
    tLangModules.get(dependency.getModuleName)
  }

}
