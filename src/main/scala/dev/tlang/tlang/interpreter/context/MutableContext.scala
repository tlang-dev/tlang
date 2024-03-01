package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.tmpl.AstContext

import scala.collection.mutable.ListBuffer

class MutableContext(scopes: ListBuffer[Scope]) {

  def removeLocalScopes(): MutableContext = {
    while (scopes.exists(_.local == true)) {
      scopes.remove(scopes.indexWhere(_.local == true))
    }
    this
  }

  def addScope(scope: Scope): MutableContext = {
    scopes += scope
    this
  }

  def addScopes(scopesToAdd: List[Scope]): MutableContext = {
    scopes ++= scopesToAdd
    this
  }

  def toContext(): Context = {
    Context(scopes.toList)
  }

}

object MutableContext {

  def toMutable(context: AstContext): MutableContext = {
//    new MutableContext(context.scopes.to(ListBuffer))
    new MutableContext(ListBuffer())
  }
}
