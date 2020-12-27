package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.tmpl.primitive.TmplPrimitiveValue

case class TmplAnnotationParam(var name: String, var value: TmplPrimitiveValue)
