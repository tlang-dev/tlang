package dev.tlang.tlang.interpreter.value

object InterValueType extends Enumeration {
  type AstValueType = Value
  val String, Long, Double, Array, Function, Entity, Model, JVM, Attr, Param, Var, StaticVar, Tmpl = Value

}
