package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.interpreter.value.InterValue

case class CallResolved(pathToFirstCall0: String, pathToFirstUsefulCall: String, nextCallIndex: Int, value: InterValue)
