package dev.tlang.tlang.ast.common

import tlang.core
import tlang.core.Type

case class ManualType(name: String, pkg: String) extends Type {
  override def getType: core.String = new core.String(pkg + "/" + name)

  override def getSimpleType: core.String = new core.String(name)

  override def getPkg: core.String = new core.String(pkg)
}
