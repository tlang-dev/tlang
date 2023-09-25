package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplSimpleValueType
import dev.tlang.tlang.ast.tmpl.call.TmplCallObjType

abstract class TmplPrimitiveValue[TYPE] extends TmplSimpleValueType[TYPE] with TmplCallObjType[TYPE]