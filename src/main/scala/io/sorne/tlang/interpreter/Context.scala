package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.ast.model.`new`.{ModelNewEntity, ModelNewValueType}

case class Context(variables: Map[String, ModelNewValueType[_]] = Map(), functions: Map[String, HelperFunc] = Map(), params: Option[List[Value[_]]] = None)
