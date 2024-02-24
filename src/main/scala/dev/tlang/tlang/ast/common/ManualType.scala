package dev.tlang.tlang.ast.common

import tlang.core
import tlang.core.Type

case class ManualType(pkg: String, name: String) extends Type {
  override def getType: core.String = {
    if (pkg.isEmpty) new core.String(name)
    else new core.String(pkg + "." + name)
  }

  override def getSimpleType: core.String = new core.String(name)

  override def getPkg: core.String = new core.String(pkg)
}
