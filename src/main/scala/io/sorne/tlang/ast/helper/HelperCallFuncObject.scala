package io.sorne.tlang.ast.helper

case class HelperCallFuncObject(name: Option[String], currying: Option[List[HelperCallFuncParam]]) extends HelperCallObjectType
