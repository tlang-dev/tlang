package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.tmpl.lang.ast.LangSimpleValueType
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallObjType

abstract class LangPrimitiveValue[TYPE] extends LangSimpleValueType[TYPE] with LangCallObjType[TYPE]