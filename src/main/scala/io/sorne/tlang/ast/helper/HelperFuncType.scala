package io.sorne.tlang.ast.helper

case class HelperFuncType(params: Option[List[HelperCurrying]], returns: Option[List[HelperParamType]]) extends HelperParamType
