package io.sorne.tlang.ast.helper.call

import io.sorne.tlang.ast.helper.HelperStatement

case class HelperCallObject(statements: List[HelperCallObjectType]) extends HelperStatement
