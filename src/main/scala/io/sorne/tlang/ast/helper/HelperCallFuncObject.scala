package io.sorne.tlang.ast.helper

case class HelperCallFuncObject(name: String, currying: Option[List[HelperCallFuncParam]]) extends HelperCallObjectType
