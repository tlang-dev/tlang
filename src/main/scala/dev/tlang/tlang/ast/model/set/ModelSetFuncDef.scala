package dev.tlang.tlang.ast.model.set

case class ModelSetFuncDef(params: Option[List[ModelSetAttribute]] = None, returns: Option[List[ModelSetAttribute]] = None) extends ModelSetValueType
