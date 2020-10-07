package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.ast.model.`new`.ModelNewEntity

case class Context(variables: Map[String, ModelNewEntity], functions: Map[String, HelperFunc])
