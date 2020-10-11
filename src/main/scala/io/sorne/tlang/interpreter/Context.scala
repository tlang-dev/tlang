package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperFunc

import scala.collection.mutable

case class Context(variables: mutable.Map[String, Value[_]] = mutable.Map(), functions: mutable.Map[String, HelperFunc] = mutable.Map())
