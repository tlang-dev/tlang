package io.sorne.tlang.libraries.tio

import io.sorne.tlang.ast.DomainModel
import io.sorne.tlang.ast.helper.HelperBlock
import io.sorne.tlang.loader.{Module, Resource}

object IOModule {

  val main: Resource = Resource("", "", "", "Main", DomainModel(None, List(
    HelperBlock(Some(List(Terminal.printlnFunc)))
  )))

  val resources: Map[String, Resource] = Map("Main" -> main)

  val ioModule: Module = Module("", resources, None, "Main")
}
