package io.sorne.tlang.libraries

import io.sorne.tlang.libraries.tio.IOModule
import io.sorne.tlang.loader.Module

object Modules {

  val tLangModules: Map[String, Module] = Map("TLang/IO" -> IOModule.ioModule)

}
