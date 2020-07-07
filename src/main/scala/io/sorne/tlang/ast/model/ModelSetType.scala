package io.sorne.tlang.ast.model

import io.sorne.tlang.ast.tmpl.TmplGeneric

case class ModelSetType(`type`: String, generics: Option[List[TmplGeneric]]) extends ModelSetValueType
