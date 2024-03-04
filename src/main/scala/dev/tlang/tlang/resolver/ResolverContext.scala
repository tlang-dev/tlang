package dev.tlang.tlang.resolver

import dev.tlang.tlang.loader.{Module, Resource}

case class ResolverContext(module: Module,
                           resource: Resource) {

  def getResType: String = getFullPkg + "/" + resource.name

  def getFullPkg: String = resource.fromRoot + "/" + resource.pkg

  def getName: String = resource.name
}
