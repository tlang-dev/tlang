package io.sorne.tlang.ast.helper.call

case class HelperCallFuncObject(name: Option[String], currying: Option[List[HelperCallFuncParam]]) extends HelperCallObjectType
