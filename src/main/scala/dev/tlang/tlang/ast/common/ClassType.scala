package dev.tlang.tlang.ast.common

import tlang.core
import tlang.core.Type

case class ClassType(clazz: Class[_]) extends Type {
  override def getType: core.String = new core.String(clazz.getPackageName + "/" + clazz.getSimpleName)

  override def getSimpleType: core.String = new core.String(clazz.getSimpleName)

  override def getPkg: core.String = new core.String(clazz.getPackageName)
}
