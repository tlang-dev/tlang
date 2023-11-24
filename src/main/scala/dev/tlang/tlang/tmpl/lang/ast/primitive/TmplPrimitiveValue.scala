package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.tmpl.lang.ast.TmplSimpleValueType
import dev.tlang.tlang.tmpl.lang.ast.call.TmplCallObjType

abstract class TmplPrimitiveValue[TYPE] extends TmplSimpleValueType[TYPE] with TmplCallObjType[TYPE]