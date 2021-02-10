package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.primitive.TmplPrimitiveValue
import io.sorne.tlang.ast.tmpl.primitive.TmplPrimitiveValue

case class TmplAnnotationParam(var name: String, var value: TmplPrimitiveValue)
