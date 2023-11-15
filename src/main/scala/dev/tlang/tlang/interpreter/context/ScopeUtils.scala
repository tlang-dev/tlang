package dev.tlang.tlang.interpreter.context

import scala.collection.mutable.ListBuffer

object ScopeUtils {

  def removeLocalScopes(scopes: ListBuffer[Scope]): ListBuffer[Scope] = {
    scopes.filter(_.local)
    scopes
  }

  def addScope(scopes: ListBuffer[Scope], scope: Scope): ListBuffer[Scope] = {
    scopes += scope
  }

  def addScopes(scopes: ListBuffer[Scope], scopesToAdd: List[Scope]): ListBuffer[Scope] = {
    scopes ++= scopesToAdd
  }

  def toContext(scopes: ListBuffer[Scope]): Context = {
    Context(scopes.toList)
  }

}
