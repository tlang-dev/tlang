package dev.tlang.tlang.ast.common.call

case class CallFuncObject(name: Option[String], currying: Option[List[CallFuncParam]]) extends CallObjectType
