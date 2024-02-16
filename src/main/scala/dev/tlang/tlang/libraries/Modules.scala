package dev.tlang.tlang.libraries

import dev.tlang.tlang.libraries.generator.GenModule
import dev.tlang.tlang.libraries.std.entity.StdEntityModule
import dev.tlang.tlang.libraries.tio.file.IOFileModule
import dev.tlang.tlang.libraries.tio.terminal.TerminalModule
import dev.tlang.tlang.libraries.tmpl._
import dev.tlang.tlang.loader.Module
import dev.tlang.tlang.loader.manifest.{Dependency, Stability}

object Modules {

  val organisation = "TLang"
  val version = "1.0.0"
  val stability: Stability.Value = Stability.ALPHA
  val releaseNumber = 1

  private val tLangModules: Map[String, Module] = Map(
    TerminalModule.getModuleName -> TerminalModule.getModule,
    GenModule.getModuleName -> GenModule.getModule,
    IOFileModule.getModuleName -> IOFileModule.getModule,
//    StdBuilderModule.getModuleName -> StdBuilderModule.getModule,
    StdEntityModule.getModuleName -> StdEntityModule.getModule,
    LangModule.getModuleName -> LangModule.getModule,
    CmdModule.getModuleName -> CmdModule.getModule,
    DataModule.getModuleName -> DataModule.getModule,
    DocModule.getModuleName -> DocModule.getModule,
    StyleModule.getModuleName -> StyleModule.getModule,
  )

  def findModule(dependency: Dependency): Option[Module] = {
    tLangModules.get(dependency.getModuleName)
  }

  def isInternalModule(module: Module): Boolean = {
    tLangModules.exists(intMod =>
      intMod._2.manifest.organisation.equals(module.manifest.organisation)
        && intMod._2.manifest.project.equals(module.manifest.project)
        && intMod._2.manifest.name.equals(module.manifest.name))
  }

}
