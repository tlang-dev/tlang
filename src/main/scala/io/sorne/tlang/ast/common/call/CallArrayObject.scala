package io.sorne.tlang.ast.common.call

case class CallArrayObject(name: String, position: SimpleValueStatement[_]) extends CallObjectType
