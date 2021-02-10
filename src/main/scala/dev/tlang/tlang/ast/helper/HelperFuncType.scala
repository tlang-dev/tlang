package dev.tlang.tlang.ast.helper

case class HelperFuncType(params: Option[List[HelperCurrying]], returns: Option[List[HelperParamType]]) extends HelperParamType
