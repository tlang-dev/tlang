package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.model.`new`.ModelNewEntity

case class Context(variables: Map[String, ModelNewEntity])
